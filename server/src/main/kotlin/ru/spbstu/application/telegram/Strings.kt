package ru.spbstu.application.telegram

import dev.inmo.tgbotapi.types.chat.PrivateChat
import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation
import ru.spbstu.application.steps.entities.BonusType
import ru.spbstu.application.steps.entities.Step

object Strings {
    val Avatars = mapOf(
        Avatar.Male to "М",
        Avatar.Female to "Ж",
        Avatar.DigitalAgile to "DigitalAgile"
    )

    val AvatarByString = Avatars.map { it.value to it.key }.toMap()

    const val AvatarsPath = "/static/avatars/"
    const val StartAvatars = AvatarsPath + "avatars.png"

    val Occupations = mapOf(
        Occupation.BachelorYear1 to "Бакалавриат 1 курс",
        Occupation.BachelorYear2 to "Бакалавриат 2 курс",
        Occupation.BachelorYear3 to "Бакалавриат 3 курс",
        Occupation.BachelorYear4 to "Бакалавриат 4 курс",
        Occupation.MasterYear1 to "Магистратура 1 курс",
        Occupation.MasterYear2 to "Магистратура 2 курс",
        Occupation.Businessman to "У меня уже есть свой бизнес!",
        Occupation.Employee to "Работаю по найму"
    )
    val BonusTypes = mapOf(
        BonusType.Bisociation to IdeaGenerationStrings.Bisociation,
        BonusType.DelphiMethod to IdeaGenerationStrings.DelphiMethod,
        BonusType.BrainstormMethod to IdeaGenerationStrings.BrainstormMethod,
        BonusType.Scamper to IdeaGenerationStrings.Scamper,
        BonusType.TrendyFriendy to IdeaGenerationStrings.TrendyFriendy
    )
    val BonusTypesByString = BonusTypes.map { it.value to it.key }.toMap()

    val OccupationByString = Occupations.map { it.value to it.key }.toMap()

    const val NotSubscribed = "Подписка неактивна"
    const val DatabaseError = "Ошибка базы данных"

    object Help {
        const val Header = "Список доступных команд:"
        const val Start = "регистрация в боте"
        const val Stats = "получение статистики о моих достижениях"
        const val Steps = "переход к меню выбора шага"
        const val Admin = "панель администратора"
    }

    const val WelcomeRequirePhone =
        "Привет, меня зовут UniStart, и я буду с тобой на протяжении всего, надеюсь, увлекательного и успешного пути по развитию собственной идеи до реального бизнеса! Для начала давай ты укажешь свой номер, чтобы получить доступ"
    const val SendPhoneButton = "Отправить номер телефона"
    const val NoPhoneInDatabase =
        "Упс, что-то пошло не так, твоего номера нет в системе, пожалуйста обратись к администратору"
    const val ChooseAvatar = "Отлично, мы в деле! Давай теперь ты выберешь мне аватар"
    const val ChooseOccupation = "Теперь давай познакомимся. Для начала, ты сейчас:"
    const val ChooseCourse = "Круто! Это отличное время для старта! А на каком курсе ты учишься?"
    const val HaveIdeaQuestion = "У тебя уже есть своя идея?"
    const val SuperIdea = "Да, хочу как следует ее прокачать!!!"
    const val NotMyIdea = "Да, не моя, но я в команде и хочу как следует ее прокачать!!!"
    const val NoIdea = "Нет, но хочу погенерить и стартануть предпринимателем!"
    const val SoSoIdea = "Вроде есть, но хочу еще погенерить и стартануть предпринимателем!"
    const val StartWithSecondStep =
        "Отлично! Ты стартуешь со второго шага, так как первый - генерация идей у тебя уже пройден. Но если ты захочешь пройти первый шаг, он тебе уже доступен и можешь выбрать и начать с него. По мере прохождения нашего пути тебе будут открываться новые шаги, но ты всегда сможешь вернуться и пройти все, которые ты уже проходил, ведь движение в бизнесе не только прямолинейно)))"
    const val StartWithFirstStep =
        "Отлично! Делаем первый шаг! По мере прохождения нашего пути тебе будут открываться новые шаги, но ты всегда сможешь вернуться и пройти все, которые ты уже проходил, ведь движение в бизнесе не только прямолинейно)))"
    const val Student = "Студент"

    const val InvalidAvatar = "Такого аватара не существует"
    const val InvalidOccupation = "Не могу понять род вашей деятельности"
    const val PhoneNumberIsAlreadyInDatabase = "Этот номер телефона уже был использован при регистрации"
    const val UserHasAlreadyBeenRegistered = "Вы уже зарегистрированы. Повторная регистрация невозможна."

    const val ChooseStep = "Выбери шаг"
    const val Step1 = "1. Генерация идей"
    const val Step2 = "2. Команда"
    const val Step3 = "3. Рынок"
    const val Step4 = "4. Бизнес-модель"
    const val GetMyStats = "Получить статистику об успехах"

    const val UnauthorizedError = "Недостаточно прав для этой команды"

    object AdminPanel {
        const val Header = "Панель администратора"
        const val InvalidDurationDays = "Введите число дней"

        const val InvalidXlsx = "Некорректный файл .xlsx. Загрузите другой файл."
        const val InvalidZip = "Некорректный zip-архив. Загрузите другой файл."

        fun InvalidSpreadsheet(rows: List<Int>) =
            "Ошибки в таблице в строках ${rows.joinToString()}. Исправьте их и загрузите файл ещё раз."

        object Menu {
            const val UploadPhoneNumbers = "Загрузить номера телефонов"
            const val StepDuration = "Длительность шагов"
            const val StatisticsSpreadsheet = "Получить сводку"
            const val ListOfAdmins = "Список администраторов"
            const val UploadTrends = "Обновить базу трендов"
        }

        object UploadTrends {
            val RequireDocumentPair = """Загрузите zip-архив. Он должен содержать:
                |1. Документ .xlsx с трендами в формате: категория, название тренда, описание, имя картинки
                |2. Сами картинки""".trimMargin()

            const val NoXlsxInArchive = "В архиве не найден .xlsx файл"
            const val Success = "База трендов обновлена!"

            fun TooFewTrends(minimum: Int) = "В каждой категории должно быть как минимум $minimum ${
                pluralize(minimum.toLong(), "тренд", "тренда", "трендов")
            }"

            fun WriteError(message: String) = "Не удалось записать данные на диск:\n$message"

            fun MissingPictures(filenames: Collection<String>) =
                "В архиве не хватает следующих картинок: ${filenames.joinToString()}. Обновите архив и загрузите ещё раз."
        }

        object StepDuration {
            const val Header = "Продолжительность шагов. Нажмите, чтобы изменить."
            fun Button(stepDuration: ru.spbstu.application.steps.entities.StepDuration): String {
                val days = stepDuration.duration.toDays()
                return "Этап ${stepDuration.step.value} – $days ${pluralize(days, "день", "дня", "дней")}"
            }

            fun Change(step: Step) =
                "Укажите новую продолжительность для этапа ${step.value} в днях"
        }

        object UploadPhoneNumbers {
            const val RequireDocument = "Загрузите документ .xlsx с номерами /n" +
                    "Формат нормеров: +7.... или 7..../n" +
                    "Формат ячеек с данными: текстовый"
            const val RequireStartDate = "Дата начала подписки для этих номеров (дд.мм.гггг)"
            const val RequireDurationDays = "Продолжительность подписки в днях"
            const val InvalidDate = "Некорректная дата. Попробуйте ещё раз."

            fun Added(count: Long) =
                "${
                    pluralize(count, "Добавлен", "Добавлено", "Добавлено")
                } $count ${
                    pluralize(count, "номер", "номера", "номеров")
                }"
        }

        object ListOfAdmins {
            const val Yes = "Да"
            const val No = "Нет"
            const val Header = "Список администраторов. Нажмите, чтобы удалить."
            const val AddAdmin = "Добавить"
            const val SendContact = "Отправьте контакт нового администратора"
            const val ErrorNoTelegram = "Это пользователь не имеет телеграмма"
            const val CantDeleteRootAdmin = "Нельзя удалить главного администратора"
            fun ConfirmationOfDeletion(chat: PrivateChat) = "Вы хотите удалить ${NameOfAdmin(chat)} из администраторов?"

            fun NameOfAdmin(chat: PrivateChat) =
                "${chat.firstName} ${chat.lastName}"

            fun NameOfRootAdmin(chat: PrivateChat) =
                "${chat.firstName} ${chat.lastName} (главный)"
        }
    }

    const val StatisticsSpreadsheetName = "Сводка UniStart"
    const val StatisticsSpreadsheetPhoneNumber = "Номер телефона"
    const val StatisticsSpreadsheetDuration = "Длительность прохождения, дни"
    const val StatisticsSpreadsheetExtraPoints = "Бонусы"
    const val StatisticsSpreadsheetOccupation = "Род занятий"

    object Notifications {
        fun NextStep(nextStep: Step) =
            "Давно тебя не видно. Пора приниматься за этап ${nextStep.value}!"
    }

    fun StatisticsSpreadsheetStep(step: Step) = "Этап ${step.value}"

    fun MyRanking(numberOfMembers: Int, myPosition: Int, myBonuses: Long) =
        "Всего участников в системе: $numberOfMembers ${
            pluralize(numberOfMembers.toLong(), "человек", "человека", "человек")
        }, ваше текущее место в рейтинге – $myPosition, накоплено бонусов – $myBonuses"

    fun NewBonusForStage(bonusValue: Long) = "Поздравляю, тебе ${
        pluralize(bonusValue, "зачислен", "зачислено", "зачислено")
    } $bonusValue ${
        pluralize(bonusValue, "бонус", "бонуса", "бонусов")
    } \uD83C\uDF1F"

    fun NewBonusForStep(bonusValue: Long, step: Step) = buildString {
        appendLine("Этап ${step.value} пройден \uD83C\uDFC6")
        if (bonusValue != 0L) {
            append(
                "Твоё вознаграждение за скорость прохождения составляет $bonusValue ${
                    pluralize(bonusValue, "бонус", "бонуса", "бонусов")
                } \uD83C\uDF1F"
            )
        }
    }
}

private fun pluralize(quantity: Long, one: String, few: String, many: String): String {
    return when {
        quantity % 10 == 1L && quantity % 100 != 11L -> one
        quantity % 10 in 2L..4L && quantity % 100 !in 12L..14L -> few
        else -> many
    }
}
