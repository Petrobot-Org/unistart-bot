package ru.spbstu.application.auth.entities.users

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.UserData

open class SubscribedUser(
    user: User
) : UserData by user, BaseUser
