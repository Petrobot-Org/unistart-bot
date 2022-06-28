package ru.spbstu.application.data.adapters

import com.squareup.sqldelight.ColumnAdapter
import ru.spbstu.application.auth.entities.Occupation

object UserOccupationAdapter : ColumnAdapter<Occupation, String> {
    override fun decode(databaseValue: String): Occupation {
        return when(databaseValue){
            Occupation.BachelorYear1.name -> Occupation.BachelorYear1
            Occupation.BachelorYear2.name -> Occupation.BachelorYear2
            Occupation.BachelorYear3.name -> Occupation.BachelorYear3
            Occupation.BachelorYear4.name -> Occupation.BachelorYear4
            Occupation.MasterYear1.name -> Occupation.MasterYear1
            Occupation.MasterYear2.name -> Occupation.MasterYear2
            Occupation.Businessman.name -> Occupation.Businessman
            Occupation.Employee.name -> Occupation.Employee
            else -> TODO("I don't understand, how it should work. Ivan")
        }
    }

    override fun encode(value: Occupation): String {
        TODO("Not yet implemented")
    }
}