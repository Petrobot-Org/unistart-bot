package ru.spbstu.application.telegram

import ru.spbstu.application.auth.entities.Avatar
import ru.spbstu.application.auth.entities.Occupation

object Strings {
    val Avatars = mapOf(
        "М" to Avatar.Male,
        "Ж" to Avatar.Female,
        "DigitalAgile" to Avatar.DigitalAgile
    )

    val Occupations = mapOf(
        "Бакалавриат 1 курс" to Occupation.BachelorYear1,
        "Бакалавриат 2 курс" to Occupation.BachelorYear2,
        "Бакалавриат 3 курс" to Occupation.BachelorYear3,
        "Бакалавриат 4 курс" to Occupation.BachelorYear4,
        "Магистратура 1 курс" to Occupation.MasterYear1,
        "Магистратура 2 курс" to Occupation.MasterYear2,
        "У меня уже есть свой бизнес!" to Occupation.Businessman,
        "Работаю по найму" to Occupation.Employee
    )

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

    const val ChooseStep = "Выбери шаг"
    const val Step1 = "1. Генерация идей"
    const val Step2 = "2. Команда"
    const val Step3 = "3. Рынок"
    const val Step4 = "4. Бизнес-модель"
    const val GetMyStats = "Получить статистику об успехах"
    const val ChooseIdeaGeneration = "Выбери технику генерации идей"
    const val Bisociation = "Бисоциации"
    const val DelphiBrainstormMethod = "Метод Дельфи/брейншторм"
    const val Scamper = "SCAMPER"
    const val TrendyFriendy = "Trendy Friendy"
    const val BackToSteps = "Обратно к шагам"

    fun MyRanking(numberOfMembers: Int, myPosition: Int, myBonuses: Long) =
        "Всего участников в системе: $numberOfMembers человек, ваше текущее место в рейтинге - $myPosition, накоплено бонусов - $myBonuses"
}
