package com.example.pam_pertemuan12.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pam_pertemuan12.R
import com.example.pam_pertemuan12.model.Mahasiswa
import com.example.pam_pertemuan12.ui.viewmodel.HomeUiState
import com.example.pam_pertemuan12.ui.viewmodel.HomeViewModel
import com.example.pam_pertemuan12.ui.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToltemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf<Mahasiswa?>(null) }

    deleteConfirmationRequired?.let { mahasiswa ->
        DeleteConfirmationDialog(
            onDeleteConfirm = {
                viewModel.deleteMhs(mahasiswa)
                deleteConfirmationRequired = null
                viewModel.getMhs() // Refresh data setelah penghapusan
            },
            onDeleteCancel = {
                deleteConfirmationRequired = null
            }
        )
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mahasiswa")
            }
        }
    ) { innerPadding ->
        HomeStatus(
            homeUiState = viewModel.mhsUiState,
            retryAction = { viewModel.getMhs() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = { mahasiswa ->
                deleteConfirmationRequired = mahasiswa
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())
        is HomeUiState.Success -> {
            if (homeUiState.data.isEmpty()) {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Tidak ada data Mahasiswa")
                }
            } else {
                ListMahasiswa(
                    listMhs = homeUiState.data,
                    onClick = { onDetailClick(it) },
                    onDeleteClick = { onDeleteClick(it) }
                )
            }
        }
        is HomeUiState.Error -> OnError(
            message = homeUiState.e.localizedMessage ?: "Terjadi kesalahan",
            retryAction = retryAction,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading),
        contentDescription = "Loading"
    )
}

@Composable
fun OnError(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    message: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = "Error"
        )
        Text(text = message, modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text("Coba Lagi")
        }
    }
}

@Composable
fun ListMahasiswa(
    listMhs: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    onDeleteClick: (Mahasiswa) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = listMhs,
            itemContent = { mhs ->
                CardMhs(
                    mhs = mhs,
                    onClick = { onClick(mhs.nim) },
                    onDeleteClick = { onDeleteClick(mhs) }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardMhs(
    mhs: Mahasiswa,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Info, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = mhs.judulSkripsi,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = mhs.nim,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(4.dp))
                IconButton(
                    onClick = { onDeleteClick(mhs) }
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Hapus Data Skripsi")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = mhs.nama,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = mhs.kelas,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Hapus Data Mahasiswa") },
        text = { Text("Apakah Anda yakin ingin menghapus data ini?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = "Batal")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Hapus")
            }
        }
    )
}
