package ru.spbstu.application.auth.entities.users

import ru.spbstu.application.auth.entities.*

open class AdminUser(
    user: User
) : SubscribedUser(user)
