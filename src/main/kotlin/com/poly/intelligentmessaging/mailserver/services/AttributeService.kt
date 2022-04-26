package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.models.NotificationModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.domain.projections.AttributeProjection
import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
class AttributeService {

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val groupAttributesRepository: GroupAttributesRepository? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val dslHandler: DSLHandler? = null

    @Autowired
    private val notificationService: NotificationService? = null

    fun getAttributes(idStaff: String): List<AttributesDTO> {
        val attributes = attributeRepository!!.getAttributes(idStaff)
        return attributesConvertToDTO(attributes, idStaff)
    }

    fun getAttributeById(attributeIdDTO: AttributeIdDTO): AttributesDTO {
        val attribute = attributeRepository!!.findById(UUID.fromString(attributeIdDTO.idAttribute)).get()
        return AttributesDTO(
            id = attribute.id.toString(),
            attributeName = attribute.name!!,
            groupName = attribute.group!!.name!!,
            type = if (attribute.expression == null) "list" else "expression",
            expression = attribute.expression,
            created = attribute.created.toString(),
            studentsDTO = attribute.students!!.associateBy {
                StudentsDTO(
                    it.id.toString(),
                    "${it.person!!.lastName} ${it.person.firstName} ${it.person.patronymic}",
                    it.person.email!!
                )
            }.keys.toMutableSet()
        )
    }

    fun getAttributesCurrentStaff(idStaff: String): List<AttributesDTO> {
        val attributes = attributeRepository!!.getAttributesCurrentStaff(idStaff)
        return attributesConvertToDTO(attributes, idStaff)
    }

    private fun attributesConvertToDTO(
        attributes: MutableList<AttributeProjection>,
        idStaff: String
    ): List<AttributesDTO> {
        val listAttributesDTO = mutableMapOf<String, AttributesDTO>()
        for (attribute in attributes) {
            val id = attribute.getId()
            if (listAttributesDTO.containsKey(id)) {
                listAttributesDTO[id]!!.students.add(attribute.getStudentId())
            } else {
                val attributeName = attribute.getAttributeName()
                val groupName = attribute.getGroupName()
                val expression = attribute.getExpression()
                val type = if (attribute.getExpression() == null) "list" else "expression"
                val status = if (type == "expression") {
                    val createdLocalDateTime = Timestamp.valueOf(attribute.getCreated()).toLocalDateTime()
                    dslHandler!!.getStatus(createdLocalDateTime, expression!!, idStaff)
                } else "success"
                val created = attribute.getCreated().split(" ")[0]
                val attributesDTO =
                    AttributesDTO(id, attributeName, groupName, expression, type, created, status = status)
                attributesDTO.students.add(attribute.getStudentId())
                listAttributesDTO[id] = attributesDTO
            }
        }
        return listAttributesDTO.values.toList()
    }

    fun createAttribute(newAttributeDTO: NewAttributeDTO): NewAttributeDTO {
        val students = getListStudentsByIds(newAttributeDTO.studentsId!!)
        val groupAttributeModel = groupAttributesRepository!!.findByNameAndStaffId(
            newAttributeDTO.groupName!!,
            UUID.fromString(newAttributeDTO.idStaff)
        )
        val staff = staffRepository!!.findById(UUID.fromString(newAttributeDTO.idStaff)).get()
        val attributeModel = AttributeModel(
            staff = staff,
            name = newAttributeDTO.name,
            expression = if (newAttributeDTO.expression == "") null else newAttributeDTO.expression,
            group = groupAttributeModel,
            students = students
        )
        println("created: $newAttributeDTO")
        attributeRepository!!.save(attributeModel)
        return newAttributeDTO
    }

    fun updateAttribute(newAttributeDTO: NewAttributeDTO): NewAttributeDTO {
        val setStudents = getListStudentsByIds(newAttributeDTO.studentsId!!)
        val groupAttributeModel = groupAttributesRepository!!.findByNameAndStaffId(
            newAttributeDTO.groupName!!,
            UUID.fromString(newAttributeDTO.idStaff)
        )
        val attributeModel = attributeRepository!!.findById(UUID.fromString(newAttributeDTO.idAttribute)).get()
        attributeModel.group = groupAttributeModel
        attributeModel.name = newAttributeDTO.name
        attributeModel.expression = if (newAttributeDTO.expression == "") null else newAttributeDTO.expression
        attributeModel.students = setStudents
        attributeModel.created = LocalDateTime.now()
        attributeRepository.save(attributeModel)
        return newAttributeDTO
    }

    private fun getListStudentsByIds(ids: MutableList<String>): Set<StudentModel> {
        val students = mutableSetOf<StudentModel>()
        ids.forEach { students.add(studentRepository!!.findById(UUID.fromString(it)).get()) }
        return students
    }

    fun deleteAttribute(attributeIdDTO: AttributeIdDTO): AttributeIdDTO {
        attributeRepository!!.deleteById(UUID.fromString(attributeIdDTO.idAttribute))
        return attributeIdDTO
    }

    fun shareAttribute(shareDTO: ShareDTO): ShareDTO {
        return notificationService!!.createNotifications(shareDTO, false)
    }

    fun acceptNotification(notification: NotificationModel, type: String) {
        if (type == "link") linkAttribute(notification)
        if (type == "copy") copyAttribute(notification)
    }

    fun linkAttribute(notification: NotificationModel) {
        val consumer = notification.consumer!!
        val attribute = notification.attribute!!
        val producerGroupName = attribute.group!!.name!!
        var attributeName = attribute.name!!
        var group = consumer.groups!!.find { it.name!!.lowercase() == producerGroupName.lowercase() }
        if (group == null) {
            group = groupAttributesRepository!!.save(GroupAttributesModel(staff = consumer, name = producerGroupName))
        } else if (group.attributes != null && group.attributes!!.isNotEmpty()) {
            val (counter, name) = checkAttributeName(0, attributeName, group.attributes!!)
            attributeName = "$name$counter"
        }
        val producer = notification.producer!!
        val newAttribute = AttributeModel(
            staff = consumer,
            producer = producer,
            group = group,
            name = attributeName,
            expression = attribute.expression
        )
        attributeRepository!!.save(newAttribute)
    }

    private fun checkAttributeName(counter: Int, name: String, attributes: Set<AttributeModel>): Pair<String, Int> {
        val newName = if (counter == 0) name else "$name$counter"
        val containsName = attributes.find { it.name!!.lowercase() == newName.lowercase() } != null
        return if (!containsName) name to counter
        else checkAttributeName(counter + 1, name, attributes)
    }

    fun copyAttribute(notification: NotificationModel) {
        TODO()
    }

//    fun getAttributesFromExpression(attribute: AttributeModel, ownerAttributes: String): Set<AttributeModel> {
//        val expression = attribute.expression!!
//        val groupAndAttributes = dslHandler!!.getAttributesFromExpression(expression)
//        val attributes = mutableSetOf<AttributeModel>()
//        groupAndAttributes.forEach {
//            val groupModel = groupAttributesRepository!!
//                .findByNameAndStaffId(it.key, UUID.fromString(ownerAttributes))
//            it.value.forEach { attr ->
//                val attributeModel = attributeRepository!!
//                    .findByIdStaffAndNameAndGroupAttribute(UUID.fromString(ownerAttributes), attr, groupModel)
//                attributes.add(attributeModel)
//            }
//        }
//        return attributes
//    }

    fun calculateExpression(expressionDTO: ExpressionDTO): ComputedExpressionDTO {
        return dslHandler!!.getComputedExpression(expressionDTO.expression!!, expressionDTO.idStaff!!, BASIC_ID_STAFF)
    }
}
