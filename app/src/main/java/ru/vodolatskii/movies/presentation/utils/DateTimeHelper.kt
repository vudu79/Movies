package ru.vodolatskii.movies.presentation.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale


object DateTimeHelper {

    fun showDateTimePicker(
        context: Context,
        onDateTimePicked: (millis: Long, formatted: String) -> Unit
    ) {
        val cal = Calendar.getInstance()

        val dateDialog = DatePickerDialog(
            context,
            { _, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)

                val is24h = DateFormat.is24HourFormat(context)
                val timeDialog = TimePickerDialog(
                    context,
                    { _, h, min ->
                        cal.set(Calendar.HOUR_OF_DAY, h)
                        cal.set(Calendar.MINUTE, min)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)

                        val formatted = String.format(
                            Locale.getDefault(),
                            "%1\$tY-%1\$tm-%1\$td в %1\$tH:%1\$tM",
                            cal
                        )
                        onDateTimePicked(cal.timeInMillis, formatted)
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    is24h
                )
                timeDialog.show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dateDialog.show()
    }
}