package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.*
import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Service
class AttributeService {

    private val logger = LoggerFactory.getLogger(AttributeService::class.java)

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

    fun getAttributes(idStaff: String): Set<AttributesDTO> {
        val attributes = attributeRepository!!.findAllByStaffIdOrStaffId(
            UUID.fromString(idStaff),
            UUID.fromString(BASIC_ID_STAFF)
        )
        return attributesConvertToDTO(attributes)
    }

    private fun getStudentsIdFromAttribute(attribute: AttributeModel): Set<String> {
        return attribute.students!!.associateBy { it.id.toString() }.keys.toSet()
    }

    fun getAttributesCurrentStaff(idStaff: String): Set<AttributesDTO> {
        val attributes = attributeRepository!!.findAllByStaffId(UUID.fromString(idStaff))
        return attributesConvertToDTO(attributes)
    }

    private fun attributesConvertToDTO(attributes: Set<AttributeModel>): Set<AttributesDTO> {
        val resultSample = mutableSetOf<AttributesDTO>()
        for (attribute in attributes) {
            val expression = attribute.expression
            val type = if (expression == null) "list" else "expression"
            val localCreated = if (attribute.dependency != null) attribute.dependency.created!! else attribute.created!!
            val status = if (type == "expression" && attribute.dependency == null) {
                val createdLocalDateTime = Timestamp.valueOf(localCreated).toLocalDateTime()
                dslHandler!!.getStatus(createdLocalDateTime, expression!!, attribute.staff!!.id.toString())
            } else "success"
            val attributesDTO = AttributesDTO(
                id = attribute.id.toString(),
                owner = attribute.staff!!.id.toString(),
                attributeName = attribute.name!!,
                groupName = attribute.group!!.name!!,
                expression = expression,
                type = type,
                link = attribute.link!!,
                created = localCreated.toLocalDate().toString(),
                students = getStudentsIdFromAttribute(attribute.dependency ?: attribute),
                status = status,
            )
            resultSample.add(attributesDTO)
        }
        return resultSample
    }

    fun getAttributeById(attributeIdDTO: AttributeIdDTO): AttributesDTO {
        val attribute = attributeRepository!!.findById(UUID.fromString(attributeIdDTO.idAttribute)).get()
        return AttributesDTO(
            id = attribute.id.toString(),
            owner = attribute.staff!!.id.toString(),
            attributeName = attribute.name!!,
            groupName = attribute.group!!.name!!,
            expression = attribute.expression,
            type = if (attribute.expression == null) "list" else "expression",
            link = attribute.link!!,
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
            link = false,
            students = students
        )
        val newAttr = attributeRepository!!.save(attributeModel)
        logger.info("${staff.person!!.email} created attribute: ${newAttr.id}")
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
        val (group, attributeName) = selectGroupAndAttributeByAttribute(attribute, consumer)
        val newAttribute = AttributeModel(
            staff = consumer,
            dependency = attribute,
            group = group,
            name = attributeName,
            expression = attribute.expression,
            link = true
        )
        attributeRepository!!.save(newAttribute)
    }

    private fun copyAttribute(notification: NotificationModel) {
        cascadeCopyAttribute(notification.attribute!!, notification.consumer!!, notification.producer!!)
    }

    fun cascadeCopyAttribute(attribute: AttributeModel, consumer: StaffModel, producer: StaffModel) {
        val cascadeAttributes = cascadeOpeningExpression(setOf(attribute), producer).sortedBy { it.created }
        val oldAndNewGroupNames = mutableMapOf<String, String>()
        val oldAndNewAttrNames = mutableMapOf<String, String>()
        for (attr in cascadeAttributes) {
            val (group, attributeName) = selectGroupAndAttributeByAttribute(attr, consumer)
            oldAndNewGroupNames[attr.group!!.name!!] = group!!.name!!
            oldAndNewAttrNames[attr.name!!] = attributeName
            var expression = attr.expression
            if (expression != null) {
                expression = dslHandler!!.refactoringExpression(expression, oldAndNewGroupNames, oldAndNewAttrNames)
            }
            val students = mutableSetOf<StudentModel>()
            attr.students!!.forEach { students.add(it) }
            val newAttribute = AttributeModel(
                staff = consumer,
                dependency = attr.dependency,
                group = group,
                name = attributeName,
                expression = expression,
                link = attr.link,
                students = students
            )
            attributeRepository!!.save(newAttribute)
        }
    }

    fun cascadeOpeningExpression(attributes: Set<AttributeModel>, producer: StaffModel): Set<AttributeModel> {
        val attrs = attributes.toMutableSet()
        for (attribute in attributes) {
            val expression = attribute.expression
            if (expression != null) {
                val attrFromExpr = dslHandler!!.getAttributeModelsFromExpression(expression, producer)
                attrs.addAll(cascadeOpeningExpression(attrFromExpr, producer))
            }
        }
        return attrs.toSet()
    }

    private fun selectGroupAndAttributeByAttribute(
        attribute: AttributeModel,
        consumer: StaffModel
    ): Pair<GroupAttributesModel?, String> {
        val producerGroupName = attribute.group!!.name!!
        val attributeName = attribute.name!!
        return selectGroupAndAttributeNames(consumer, attributeName, producerGroupName)
    }

    fun selectGroupAndAttributeByFilter(
        filter: FilterModel,
        consumer: StaffModel
    ): Pair<GroupAttributesModel?, String> {
        val producerGroupName = filter.name!!
        val attributeName = filter.name!!
        return selectGroupAndAttributeNames(consumer, attributeName, producerGroupName)
    }

    private fun selectGroupAndAttributeNames(
        consumer: StaffModel,
        attrName: String,
        grName: String
    ): Pair<GroupAttributesModel, String> {
        var attributeName = attrName
        val groups = groupAttributesRepository!!.findAllByStaff(consumer)
        var group = groups.find { it.name!!.lowercase() == grName.lowercase() }
        if (group == null) {
            group = groupAttributesRepository.save(GroupAttributesModel(staff = consumer, name = grName))
        } else if (group.attributes != null && group.attributes!!.isNotEmpty()) {
            val (name, counter) = checkAttributeName(0, attributeName, group.attributes!!)
            attributeName = if (counter == 0) name else "$name$counter"
        }
        return group!! to attributeName
    }

    private fun checkAttributeName(counter: Int, name: String, attributes: Set<AttributeModel>): Pair<String, Int> {
        val newName = if (counter == 0) name else "$name$counter"
        val containsName = attributes.find { it.name!!.lowercase() == newName.lowercase() } != null
        return if (!containsName) name to counter
        else checkAttributeName(counter + 1, name, attributes)
    }

    fun calculateExpression(expressionDTO: ExpressionDTO): ComputedExpressionDTO {
        return dslHandler!!.getComputedExpression(expressionDTO.expression!!, expressionDTO.idStaff!!, BASIC_ID_STAFF)
    }
}
