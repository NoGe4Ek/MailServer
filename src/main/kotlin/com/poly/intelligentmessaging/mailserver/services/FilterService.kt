package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.domain.EmailData
import com.poly.intelligentmessaging.mailserver.domain.dto.FilterIdDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.FiltersDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.NewFilterDTO
import com.poly.intelligentmessaging.mailserver.domain.dto.ShareDTO
import com.poly.intelligentmessaging.mailserver.domain.models.EmailModel
import com.poly.intelligentmessaging.mailserver.domain.models.FilterModel
import com.poly.intelligentmessaging.mailserver.domain.models.StaffModel
import com.poly.intelligentmessaging.mailserver.domain.models.StudentModel
import com.poly.intelligentmessaging.mailserver.repositories.EmailRepository
import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class FilterService {

    @Autowired
    val filterRepository: FilterRepository? = null

    @Autowired
    val studentRepository: StudentRepository? = null

    @Autowired
    val staffRepository: StaffRepository? = null

    @Autowired
    val emailRepository: EmailRepository? = null

    @Autowired
    val emailBox: EmailBox? = null

    fun getFilters(idStaff: String, isShort: Boolean): List<FiltersDTO> {
        val listFiltersDTO = mutableMapOf<String, FiltersDTO>()
        val listFilterProjection = filterRepository!!.getFilters(idStaff)
        for (filter in listFilterProjection) {
            val filterId = filter.getId()
            if (listFiltersDTO.containsKey(filterId)) {
                listFiltersDTO[filterId]!!.students.add(filter.getIdStudent())
            } else {
                val filterName = filter.getFilterName()
                val mail = filter.getEmail()
                val type = if (filter.getExpression() == null) "list" else "expression"
                val mode = filter.getMode()
                val created = filter.getCreated().split(" ")[0]
                val mailCounter = if (mode == "manual" && !isShort) getNumberOfMails(filterId) else null
                val filtersDTO = FiltersDTO(filterId, filterName, mail, type, mode, created, mailCounter)
                filtersDTO.students.add(filter.getIdStudent())
                listFiltersDTO[filterId] = filtersDTO
            }
        }
        return listFiltersDTO.values.toList()
    }

    fun createFilter(newFilterDTO: NewFilterDTO, idStaff: String): NewFilterDTO {
        val students = mutableSetOf<StudentModel>()
        for (id in newFilterDTO.studentsId!!) {
            val studentModel = studentRepository!!.findById(UUID.fromString(id)).get()
            students.add(studentModel)
        }
        val staff = staffRepository!!.findById(UUID.fromString(idStaff)).get()

        val (send, answer) = generateMailData(staff, newFilterDTO.name!!)

        val emailSend = EmailModel(
            email = send.email,
            password = send.password,
            mailDirectory = send.mailDirectory,
        )
        val emailAnswer = EmailModel(
            email = answer.email,
            password = answer.password,
            mailDirectory = answer.mailDirectory,
        )
        val filter = FilterModel(
            staff = staff,
            emailSend = emailRepository!!.save(emailSend),
            emailAnswer = emailRepository!!.save(emailAnswer),
            name = newFilterDTO.name,
            mode = newFilterDTO.mailOption,
            autoForward = newFilterDTO.mailOption == "auto",
            students = students
        )
        filterRepository!!.save(filter)
        return newFilterDTO
    }

    fun updateFilter(newFilterDTO: NewFilterDTO): NewFilterDTO {
        val students = mutableSetOf<StudentModel>()
        for (id in newFilterDTO.studentsId!!) {
            val studentModel = studentRepository!!.findById(UUID.fromString(id)).get()
            students.add(studentModel)
        }
        val filter = filterRepository!!.findById(UUID.fromString(newFilterDTO.idFilter)).get()
        filter.name = newFilterDTO.name
        filter.mode = newFilterDTO.mailOption
        filter.autoForward = newFilterDTO.mailOption == "auto"
        filter.students = students
        filterRepository!!.save(filter)
        return newFilterDTO
    }

    fun deleteFilter(filterIdDTO: FilterIdDTO): FilterIdDTO {
        filterRepository!!.deleteById(UUID.fromString(filterIdDTO.idFilter))
        return filterIdDTO
    }

    fun getEmails(filterIdDTO: FilterIdDTO): FilterIdDTO {
        val filterModel = filterRepository!!.findById(UUID.fromString(filterIdDTO.idFilter)).get()
        val emailAnswer = filterModel.emailAnswer!!
        val staffEmail = filterModel.staff!!.person!!.email!!
        val emailAuth = EmailAuthenticator(emailAnswer.email!!, emailAnswer.password!!)
        emailBox!!.getEmails(emailAuth, setOf(staffEmail))
        return filterIdDTO
    }

    fun sendEmails(filterModel: FilterModel) {
        val recipient = filterModel.students!!.associateWith { it.person!!.email!! }.values.toSet()
        val emailSend = filterModel.emailSend!!
        val emailAuth = EmailAuthenticator(emailSend.email!!, emailSend.password!!)
        emailBox!!.sendEmails(emailAuth, recipient, filterModel.emailAnswer!!.email!!)
    }

    fun shareFilter(shareDTO: ShareDTO): ShareDTO {
        val filter = filterRepository!!.findById(UUID.fromString(shareDTO.id)).get()
        for (staffId in shareDTO.staffIds!!) {
            val staff = staffRepository!!.findById(UUID.fromString(staffId)).get()
            val (send, answer) = generateMailData(staff, filter.name!!)
            val students = mutableSetOf<StudentModel>()
            filter.students!!.forEach { students.add(it) }
            val filterModel = FilterModel(
                staff = staff,
                emailSend = emailRepository!!.save(
                    EmailModel(
                        email = send.email,
                        password = send.password,
                        mailDirectory = send.mailDirectory
                    )
                ),
                emailAnswer = emailRepository!!.save(
                    EmailModel(
                        email = answer.email,
                        password = answer.password,
                        mailDirectory = answer.mailDirectory
                    )
                ),
                name = filter.name,
                mode = filter.mode,
                autoForward = filter.autoForward,
                expression = filter.expression,
                students = students
            )
            filterRepository!!.save(filterModel)
        }
        return shareDTO
    }

    private fun generateMailData(staff: StaffModel, filterName: String): Pair<EmailData, EmailData> {
        val domain = "@poly-sender.ru"
        val nameSend = "f" + (staff.id.toString() + filterName + Random.nextInt(10)).hashCode().toString() + "_s"
        val nameAnswer = "f" + (staff.id.toString() + filterName + Random.nextInt(10)).hashCode().toString() + "_a"
        val send = EmailData(nameSend + domain, staff.id.toString() + "_s", "/$nameSend")
        val answer = EmailData(nameAnswer + domain, staff.id.toString() + "a", "/$nameAnswer")
        return send to answer
    }

    private fun getNumberOfMails(idFilter: String): Int {
        val filterModel = filterRepository!!.findById(UUID.fromString(idFilter)).get()
        val emailAnswer = filterModel.emailAnswer!!
        val emailAuth = EmailAuthenticator(emailAnswer.email!!, emailAnswer.password!!)
        return emailBox!!.getNumberOfMails(emailAuth)
    }
}
