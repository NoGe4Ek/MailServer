package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.AttributeModel
import com.poly.intelligentmessaging.mailserver.domain.models.GroupAttributesModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.domain.projections.AttributeProjection
import com.poly.intelligentmessaging.mailserver.repositories.AttributeRepository
import com.poly.intelligentmessaging.mailserver.repositories.GroupAttributesRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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

    private val basicIdStaff = "ad7a8951-2f95-4619-802b-1285c3279623"

    fun getAttributes(idStaff: String): List<AttributesDTO> {
        val attributes = attributeRepository!!.getAttributes(idStaff)
        return attributesConvertToDTO(attributes)
    }

    fun getAttributesCurrentStaff(idStaff: String): List<AttributesDTO> {
        val attributes = attributeRepository!!.getAttributesCurrentStaff(idStaff)
        return attributesConvertToDTO(attributes)
    }

    private fun attributesConvertToDTO(attributes: MutableList<AttributeProjection>): List<AttributesDTO> {
        val listAttributesDTO = mutableMapOf<String, AttributesDTO>()
        for (attribute in attributes) {
            val id = attribute.getId()
            if (listAttributesDTO.containsKey(id)) {
                listAttributesDTO[id]!!.students.add(attribute.getStudentId())
            } else {
                val attributeName = attribute.getAttributeName()
                val groupName = attribute.getGroupName()
                val type = if (attribute.getExpression() == null) "list" else "expression"
                val created = attribute.getCreated().split(" ")[0]
                val attributesDTO = AttributesDTO(id, attributeName, groupName, type, created)
                attributesDTO.students.add(attribute.getStudentId())
                listAttributesDTO[id] = attributesDTO
            }
        }
        return listAttributesDTO.values.toList()
    }

    fun createAttribute(newAttributeDTO: NewAttributeDTO, idStaff: String): NewAttributeDTO {
        val students = mutableSetOf<StudentModel>()
        newAttributeDTO.studentsId!!.forEach {
            students.add(studentRepository!!.findById(UUID.fromString(it)).get())
        }
        val groupAttributeModel = groupAttributesRepository!!.findByName(newAttributeDTO.groupName!!)
        val staff = staffRepository!!.findById(UUID.fromString(idStaff)).get()
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
        val setStudents = mutableSetOf<StudentModel>()
        newAttributeDTO.studentsId!!.forEach {
            setStudents.add(studentRepository!!.findById(UUID.fromString(it)).get())
        }
        val groupAttributeModel = groupAttributesRepository!!.findByName(newAttributeDTO.groupName!!)
        val attributeModel = attributeRepository!!.findById(UUID.fromString(newAttributeDTO.idAttribute)).get()
        attributeModel.group = groupAttributeModel
        attributeModel.name = newAttributeDTO.name
        attributeModel.expression = if (newAttributeDTO.expression == "") null else newAttributeDTO.expression
        attributeModel.students = setStudents
        attributeRepository.save(attributeModel)
        return newAttributeDTO
    }

    fun deleteAttribute(attributeIdDTO: AttributeIdDTO): AttributeIdDTO {
        attributeRepository!!.deleteById(UUID.fromString(attributeIdDTO.idAttribute))
        return attributeIdDTO
    }

    fun shareAttribute(shareDTO: ShareDTO): ShareDTO {
        val attribute = attributeRepository!!.findById(UUID.fromString(shareDTO.id)).get()
        for (staffId in shareDTO.staffIds!!) {
            val staff = staffRepository!!.findById(UUID.fromString(staffId)).get()
            val groupReference = attribute.group!!
            val students = mutableSetOf<StudentModel>()
            attribute.students!!.forEach { students.add(it) }
            val group = staff.groups!!.find { it.name == groupReference.name }
            val attributeModel = AttributeModel(
                staff = staff,
                group = group ?: groupAttributesRepository!!.save(
                    GroupAttributesModel(staff = staff, name = groupReference.name)
                ),
                name = attribute.name,
                expression = attribute.expression,
                students = students
            )
            attributeRepository.save(attributeModel)
        }
        return shareDTO
    }

    fun calculateExpression(expressionDTO: ExpressionDTO, currentIdStaff: String): ComputedExpressionDTO {
        return dslHandler!!.getComputedExpression(expressionDTO.expression!!, currentIdStaff, basicIdStaff)
    }
}
