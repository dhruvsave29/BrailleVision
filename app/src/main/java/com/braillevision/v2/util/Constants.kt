package com.braillevision.v2.util

object Constants {
    
    const val MODEL_FILENAME = "best_float32.tflite"
    const val DICTIONARY_FILENAME = "frequency_dictionary.txt"
    
    const val INPUT_SIZE = 320
    const val NUM_CLASSES = 26
    
    const val CONFIDENCE_THRESHOLD = 0.5f
    const val NMS_THRESHOLD = 0.4f
    
    val BRAILLE_CHARACTERS = listOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    )
    
    fun getClassCharacter(classIndex: Int): Char {
        return if (classIndex in BRAILLE_CHARACTERS.indices) {
            BRAILLE_CHARACTERS[classIndex]
        } else {
            '?'
        }
    }
}
