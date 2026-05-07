package org.wit.webappproject

/*
* References:
*
* Android Internal Storage:
* https://developer.android.com/training/data-storage/app-specific
*
* FileInputStream Documentation:
* https://developer.android.com/reference/java/io/FileInputStream
*
* FileOutputStream Documentation:
* https://developer.android.com/reference/java/io/FileOutputStream
*
* BufferedReader Documentation:
* https://developer.android.com/reference/java/io/BufferedReader
*
* InputStreamReader Documentation:
* https://developer.android.com/reference/java/io/InputStreamReader
*
* OutputStreamWriter Documentation:
* https://developer.android.com/reference/java/io/OutputStreamWriter
*
* Timber Logging Library:
* https://github.com/JakeWharton/timber
*/

import android.content.Context
import timber.log.Timber
import java.io.*

// Writes JSON data into android internal storage. Existing file contents are overwritten.
fun write(context: Context, fileName: String, data: String) {
    try {
    // Open internal file output stream. MODE_PRIVATE ensures file is only accessible by this application.
        val outputStreamWriter =
            OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))

        // Write JSON string into file.
        outputStreamWriter.write(data)
        // Close output stream
        outputStreamWriter.close()

    } catch (e: Exception) {
        // Log write errors using Timber
        Timber.e("Cannot write file: %s", e.toString())
    }
}

// Reads JSON data from internal storage and returns file contents as a string.
fun read(context: Context, fileName: String): String {
    var str = ""

    try {
        // Open file input stream
        val inputStream = context.openFileInput(fileName)

        if (inputStream != null) {
            // Convert byte stream into readable text
            val inputStreamReader = InputStreamReader(inputStream)
            // BufferedReader improves reading performance
            val bufferedReader = BufferedReader(inputStreamReader)

            val stringBuilder = StringBuilder()

            var line: String?

            // Read file line by line
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            // Close input stream.
            inputStream.close()
            // Convert StringBuilder into String.
            str = stringBuilder.toString()
        }

    } catch (e: FileNotFoundException) {
        // File may not exist on first app launch
        Timber.e("File not found: %s", e.toString())

    } catch (e: IOException) {
        // Handles read failures.
        Timber.e("Cannot read file: %s", e.toString())
    }

    return str
}

// Checks whether the specified save file already exists in internal storage.
fun exists(context: Context, filename: String): Boolean {
    val file = context.getFileStreamPath(filename)
    return file.exists()
}