package com.example.roomnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.* import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.roomnote.data.AppDatabase
import com.example.roomnote.data.Note
import com.example.roomnote.data.NoteRepository
import com.example.roomnote.ui.theme.RoomNoteTheme
import com.example.roomnote.data.NoteViewModel
import com.example.roomnote.data.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(applicationContext)
        val repo = NoteRepository(db.noteDao())
        val factory = NoteViewModelFactory(repo)

        setContent {
            RoomNoteTheme {
                val vm: NoteViewModel = viewModel(factory = factory)
                Surface(modifier = Modifier.fillMaxSize()) {
                    NoteScreen(vm)
                }
            }
        }
    }
}

@Composable
fun NoteScreen(vm: NoteViewModel) {
    val notes by vm.notes.collectAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = if (editingId == null) "Tambah Note" else "Edit Note",
            style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = title, onValueChange = { title = it
        }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it },
            label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = {
                if (editingId == null) vm.insert(title, desc)
                else {
                    vm.update(editingId ?: 0L, title, desc)
                    editingId = null
                }
                title = ""; desc = ""
            }) {
                Text(if (editingId == null) "Save" else "Update")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (editingId != null) {
                OutlinedButton(onClick = { editingId = null; title =
                    ""; desc = "" }) { Text("Cancel") }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text("List Notes", style =
            MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (notes.isEmpty()) {
            Text("Belum ada data")
        } else {
            LazyColumn {
                items(notes) { note ->
                    NoteItem(note,
                        onClick = {
                            editingId = note.id
                            title = note.title
                            desc = note.description
                        },
                        onDelete = { vm.delete(note) }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement
        = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, style =
                    MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(note.description, style =
                    MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription =
                    "Delete")
            }
        }
    }
}