package com.example.pam_pertemuan12.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_pertemuan12.model.Mahasiswa
import com.example.pam_pertemuan12.repository.RepositoryMhs
import kotlinx.coroutines.launch

class InsertViewModel (
    private val mhs: RepositoryMhs
) : ViewModel() {
    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set

    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set

    // Memperbarui state berdasarkan input pengguna
    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent
        )
    }

    // Validasi data input pengguna
    fun validateFields() : Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "NIM tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "Nama tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "Alamat tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "Kelas tidak boleh kosong",
            gender = if (event.gender.isNotEmpty()) null else "Jenis Kelamin tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "Angkatan tidak boleh kosong",
            judulSkripsi = if (event.judulSkripsi.isNotEmpty()) null else "Judul Skripsi tidak boleh kosong",
            dospem1 = if (event.dospem1.isNotEmpty()) null else "Dosen Pembimbing 1 tidak boleh kosong",
            dospem2 = if (event.dospem2.isNotEmpty()) null else "Dosen Pembimbing 2 tidak boleh kosong",
            )

        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMhs() {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMhs(uiEvent.insertUiEvent.toMhsModel())
                    uiState = FormState.Success("Data berhasil disimpan")
                } catch (e: Exception) {
                    uiState = FormState.Error("Data gagal disimpan")
                }
            }
        } else {
            uiState = FormState.Error("Data tidak valid")
        }
    }

    fun resetForm() {
        uiEvent = InsertUiState()
        uiState = FormState.Idle
    }

    fun resetSnackBarMessage() {
        uiState = FormState.Idle
    }
}

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    data class Success (val message: String) : FormState()
    data class Error (val message: String) : FormState()
}

data class InsertUiState(
    val insertUiEvent: MahasiswaEvent = MahasiswaEvent(),
    val isEntryValid: FormErrorState = FormErrorState()
)

data class FormErrorState (
    val nim: String? = null,
    val nama: String? = null,
    val alamat: String? = null,
    val kelas: String? = null,
    val gender: String? = null,
    val angkatan: String? = null,
    val judulSkripsi: String? = null,
    val dospem1: String? = null,
    val dospem2: String? = null
) {
    fun isValid() : Boolean {
        return nim == null && nama == null && alamat == null &&
                kelas == null && gender == null && angkatan == null &&
                    judulSkripsi == null && dospem1 == null && dospem2 == null
    }
}

// Data class variabel yang menyimpan data input form
data class MahasiswaEvent (
    val nim: String = "",
    val nama: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val gender: String = "",
    val angkatan: String = "",
    val judulSkripsi: String = "",
    val dospem1: String = "",
    val dospem2: String = ""
)

// Menyimpan input form ke dalam entity
fun MahasiswaEvent.toMhsModel(): Mahasiswa = Mahasiswa (
    nim = nim,
    nama = nama,
    alamat = alamat,
    kelas = kelas,
    gender = gender,
    angkatan = angkatan,
    judulSkripsi = judulSkripsi,
    dospem1 = dospem1,
    dospem2 = dospem2
)