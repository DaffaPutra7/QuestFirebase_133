package com.example.pam_pertemuan12.model

data class Mahasiswa(
    val nim: String,
    val nama: String,
    val alamat: String,
    val kelas: String,
    val gender: String,
    val angkatan: String
){
    constructor(): this ("", "", "", "", "", "")
}
