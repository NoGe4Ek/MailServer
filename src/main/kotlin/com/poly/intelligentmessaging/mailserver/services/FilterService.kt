package com.poly.intelligentmessaging.mailserver.services

import com.poly.intelligentmessaging.mailserver.components.DSLHandler
import com.poly.intelligentmessaging.mailserver.components.EmailBox
import com.poly.intelligentmessaging.mailserver.domain.EmailData
import com.poly.intelligentmessaging.mailserver.domain.dto.*
import com.poly.intelligentmessaging.mailserver.domain.models.*
import com.poly.intelligentmessaging.mailserver.repositories.*
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

    @Autowired
    private val attributeService: AttributeService? = null

    @Autowired
    private val attributeRepository: AttributeRepository? = null

    fun getFilters(idStaff: String, isShort: Boolean): MutableSet<FiltersDTO> {
        val resultSample = mutableSetOf<FiltersDTO>()
        val filters = filterRepository!!.findAllByStaffId(UUID.fromString(idStaff))
        for (filter in filters) {
            val expression = filter.expression
            val type = if (expression == null) "list" else "expression"
            val localCreated = if (filter.dependency != null) filter.dependency.created!! else filter.created!!
            val status = if (type == "expression" && filter.dependency == null) {
                val createdLocalDateTime = Timestamp.valueOf(localCreated).toLocalDateTime()
                dslHandler!!.getStatus(createdLocalDateTime, expression!!, filter.staff!!.id.toString())
            } else "success"
            val filtersDTO = FiltersDTO(
                id = filter.id.toString(),
                filterName = filter.name!!,
                mail = filter.emailSend!!.email!!,
                expression = expression,
                type = type,
                mode = filter.mode!!,
                created = filter.created!!.toLocalDate().toString(),
                link = filter.link!!,
                mailCounter = if (filter.mode == "manual" && !isShort) getNumberOfMails(filter.id.toString()) else null,
                students = getStudentsIdFromFilter(filter.dependency ?: filter),
                status = status
            )
            resultSample.add(filtersDTO)
        }
        return resultSample
    }

    private fun getStudentsIdFromFilter(filter: FilterModel): Set<String> {
        return filter.students!!.associateBy { it.id.toString() }.keys.toSet()
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
            link = filter.link!!,
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
            link = false,
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
        val consumer = notification.consumer!!
        val filter = notification.filter!!
        val filterName = selectFilterName(filter, consumer)
        val (send, answer) = generateMailData(consumer, filterName)
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
        val newFilter = FilterModel(
            staff = consumer,
            dependency = filter,
            emailSend = emailRepository!!.save(emailSend),
            emailAnswer = emailRepository.save(emailAnswer),
            name = filterName,
            mode = filter.mode,
            autoForward = filter.autoForward,
            expression = filter.expression,
            link = true
        )
        filterRepository!!.save(newFilter)
    }

    private fun copyFilter(notification: NotificationModel) {
        val filter = notification.filter!!
        val consumer = notification.consumer!!
        var newExpression: String? = null
        val students = mutableSetOf<StudentModel>()
        filter.students!!.forEach { students.add(it) }
        if (filter.expression != null) {
            val (group, attributeName) = attributeService!!.selectGroupAndAttributeByFilter(filter)
            val attribute = AttributeModel(
                staff = consumer,
                group = group,
                name = attributeName,
                expression = filter.expression,
                students = students,
                link = false,
                created = LocalDateTime.now()
            )
            attributeService.cascadeCopyAttribute(attribute, notification.consumer, notification.producer!!)
            val groupName = group!!.name!!.replace(" ", "_")
            newExpression = "$groupName[${attributeName.replace(" ", "_")}]".lowercase()
        }
        val filterName = selectFilterName(filter, consumer)
        val (send, answer) = generateMailData(consumer, filterName)
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
        val newFilter = FilterModel(
            staff = consumer,
            emailSend = emailRepository!!.save(emailSend),
            emailAnswer = emailRepository.save(emailAnswer),
            name = filterName,
            mode = filter.mode,
            autoForward = filter.autoForward,
            expression = newExpression,
            link = false,
            students = students
        )
        filterRepository!!.save(newFilter)
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

    private fun selectFilterName(filter: FilterModel, consumer: StaffModel): String {
        var filterName = filter.name!!
        if (consumer.filters != null && consumer.filters.isNotEmpty()) {
            val (name, counter) = checkAttributeName(0, filterName, consumer.filters)
            filterName = if (counter == 0) name else "$name$counter"
        }
        return filterName
    }

    private fun checkAttributeName(counter: Int, name: String, filters: Set<FilterModel>): Pair<String, Int> {
        val newName = if (counter == 0) name else "$name$counter"
        val containsName = filters.find { it.name!!.lowercase() == newName.lowercase() } != null
        return if (!containsName) name to counter
        else checkAttributeName(counter + 1, name, filters)
    }
}
