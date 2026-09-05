package com.orbit.spatialjournal.export

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/** Bundles a set of already-generated export files (JSON + media) into a single .zip. */
class ZipExporter @Inject constructor() {
    fun zip(files: List<File>, destination: File) {
        ZipOutputStream(destination.outputStream()).use { zos ->
            for (file in files) {
                zos.putNextEntry(ZipEntry(file.name))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }
}
