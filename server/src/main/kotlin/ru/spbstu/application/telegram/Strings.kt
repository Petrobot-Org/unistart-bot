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

    const val TrendyFriendyDescription = "Trendy Friendy"
    const val TrendyFriendyOpen = "Start"
}