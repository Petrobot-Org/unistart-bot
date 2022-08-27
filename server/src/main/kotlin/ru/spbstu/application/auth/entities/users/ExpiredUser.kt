package ru.spbstu.application.auth.entities.users

import ru.spbstu.application.auth.entities.User
import ru.spbstu.application.auth.entities.UserData

class ExpiredUser(
    user: User
) : UserData by user, BaseUser
