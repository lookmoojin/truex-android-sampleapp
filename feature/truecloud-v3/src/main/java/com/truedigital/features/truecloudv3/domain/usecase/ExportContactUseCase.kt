package com.truedigital.features.truecloudv3.domain.usecase

import android.os.Environment
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileWriter
import java.util.Calendar
import javax.inject.Inject

interface ExportContactUseCase {
    fun execute(contactList: List<ContactTrueCloudModel>): Flow<Unit>
}

class ExportContactUseCaseImpl @Inject constructor() : ExportContactUseCase {
    companion object {
        private const val VCF_FILE_PREFIX_NAME = "truecloud_contacts_"
        private const val VCF_FILE_SUFFIX_NAME = ".vcf"
        private const val VCF_FILE_PATH = "/TrueCloud"
    }

    override fun execute(contactList: List<ContactTrueCloudModel>): Flow<Unit> {
        return flow {
            val vdfDirectory =
                File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ).path + VCF_FILE_PATH
                )
            if (!vdfDirectory.exists()) {
                vdfDirectory.mkdirs()
            }
            val vcfFile = File(
                vdfDirectory,
                VCF_FILE_PREFIX_NAME + Calendar.getInstance()
                    .timeInMillis + VCF_FILE_SUFFIX_NAME
            )
            val fw = FileWriter(vcfFile)
            contactList.forEach { contact ->
                fw.write("BEGIN:VCARD\r\n")
                fw.write("VERSION:3.0\r\n")
                fw.write("FN:" + contact.firstName + "\r\n")
                contact.tel.forEach { phoneNumber ->
                    fw.write("TEL;TYPE=" + phoneNumber.type + ",VOICE:" + phoneNumber.number + "\r\n")
                }
                fw.write("PHOTO;ENCODING=B;TYPE=JPEG:" + contact.picture + "\r\n")
                fw.write("EMAIL;TYPE=PREF,INTERNET:" + contact.email + "\r\n")
                fw.write("END:VCARD\r\n")
            }
            fw.close()
            emit(Unit)
        }.catch {}
    }
}
