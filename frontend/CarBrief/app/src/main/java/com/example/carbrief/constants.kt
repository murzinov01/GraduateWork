package com.example.carbrief

const val maxUserNameLength = 256
const val minUserNameLength = 4
const val minPasswordLength = 6


var picturesOnLabelsMapping = mapOf(
    Labels.CLIMATE_CONTROL.text to R.drawable.climat_control,
    Labels.MUSIC_SYSTEM.text to R.drawable.music_system,
    Labels.AIR_BLOWER.text to R.drawable.music_system,
    Labels.DISPLAY.text to R.drawable.display,
    Labels.LIGHT_CONTROLLER.text to R.drawable.light_switcher,
    Labels.STEERING_BUTTONS.text to R.drawable.steering_wheel,
    Labels.GEAR_LEVER.text to R.drawable.drive_switcher,
    Labels.DRIVE_SWITCHING.text to R.drawable.i_drive_conrtoller,
)

var topicsOnLabelsMapping = mapOf(
    Labels.CLIMATE_CONTROL.text to R.string.climate_control_topic,
    Labels.MUSIC_SYSTEM.text to R.string.music_system_topic,
    Labels.AIR_BLOWER.text to R.string.air_blower_topic,
    Labels.DISPLAY.text to R.string.display_topic,
    Labels.LIGHT_CONTROLLER.text to R.string.light_controller_topic,
    Labels.STEERING_BUTTONS.text to R.string.steering_buttons_topic,
    Labels.GEAR_LEVER.text to R.string.gear_lever_topic,
    Labels.DRIVE_SWITCHING.text to R.string.drive_switching_topic,
)