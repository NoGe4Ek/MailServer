package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.domain.EmailData
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.*
import com.poly.intelligentmessaging.mailserver.repositories.EmailRepository
import com.poly.intelligentmessaging.mailserver.repositories.FilterRepository
import com.poly.intelligentmessaging.mailserver.repositories.StaffRepository
import com.poly.intelligentmessaging.mailserver.repositories.StudentRepository
import com.poly.intelligentmessaging.mailserver.util.BASIC_ID_STAFF
import com.poly.intelligentmessaging.mailserver.util.EmailAuthenticator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

@Service
class FilterService {

    @Autowired
    private val filterRepository: FilterRepository? = null

    @Autowired
    private val studentRepository: StudentRepository? = null

    @Autowired
    private val staffRepository: StaffRepository? = null

    @Autowired
    private val emailRepository: EmailRepository? = null

    @Autowired
    private val emailBox: EmailBox? = null

    @Autowired
    private val dslHandler: DSLHandler? = null

    @Autowired
    private val notificationService: NotificationService? = null

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
                val expression = filter.getExpression()
                val type = if (expression == null) "list" else "expression"
                val status = if (type == "expression") {
                    val createdLocalDateTime = Timestamp.valueOf(filter.getCreated()).toLocalDateTime()
                    dslHandler!!.getStatus(createdLocalDateTime, expression!!, idStaff)
                } else "success"
                val mode = filter.getMode()
                val created = filter.getCreated().split(" ")[0]
                val mailCounter = if (mode == "manual" && !isShort) getNumberOfMails(filterId) else null
                val filtersDTO = FiltersDTO(
                    filterId,
                    filterName,
                    mail,
                    expression,
                    type,
                    mode,
                    created,
                    mailCounter,
                    status = status
                )
                filtersDTO.students.add(filter.getIdStudent())
                listFiltersDTO[filterId] = filtersDTO
            }
        }
        return listFiltersDTO.values.toList()
    }

    fun getFilterById(filterIdDTO: FilterIdDTO): FiltersDTO {
        val filter = filterRepository!!.findById(UUID.fromString(filterIdDTO.idFilter)).get()
        return FiltersDTO(
            id = filter.id.toString(),
            filterName = filter.name!!,
            mail = filter.emailSend!!.email!!,
            type = if (filter.expression == null) "list" else "expression",
            mode = filter.mode!!,
            expression = filter.expression,
            created = "",
            studentsDTO = filter.students!!.associateBy {
                StudentsDTO(
                    it.id.toString(),
                    "${it.person!!.lastName} ${it.person.firstName} ${it.person.patronymic}",
                    it.person.email!!
                )
            }.keys.toMutableSet()
        )
    }

    fun createFilter(newFilterDTO: NewFilterDTO): NewFilterDTO {
        val students = mutableSetOf<StudentModel>()
        for (id in newFilterDTO.studentsId!!) {
            val studentModel = studentRepository!!.findById(UUID.fromString(id)).get()
            students.add(studentModel)
        }
        val staff = staffRepository!!.findById(UUID.fromString(newFilterDTO.idStaff!!)).get()

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
            emailAnswer = emailRepository.save(emailAnswer),
            name = newFilterDTO.name,
            expression = if (newFilterDTO.expression == "") null else newFilterDTO.expression,
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
        filter.expression = if (newFilterDTO.expression == "") null else newFilterDTO.expression
        filter.autoForward = newFilterDTO.mailOption == "auto"
        filter.students = students
        filter.created = LocalDateTime.now()
        filterRepository.save(filter)
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
        return notificationService!!.createNotifications(shareDTO, true)
    }

    fun acceptNotification(notification: NotificationModel, type: String) {
        if (type == "link") linkFilter(notification)
        if (type == "copy") copyFilter(notification)
    }

    private fun linkFilter(notification: NotificationModel) {
        TODO()
    }

    private fun copyFilter(notification: NotificationModel) {
        TODO()
    }

    fun calculateExpression(expressionDTO: ExpressionDTO): ComputedExpressionDTO {
        return dslHandler!!.getComputedExpression(expressionDTO.expression!!, expressionDTO.idStaff!!, BASIC_ID_STAFF)
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
