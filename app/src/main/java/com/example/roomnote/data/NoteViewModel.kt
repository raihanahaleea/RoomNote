package com.example.roomnote.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
class NoteViewModel(private val repo: NoteRepository) : ViewModel() {
    val notes: StateFlow<List<Note>> =
        repo.getAllNotes().stateIn(viewModelScope,
            SharingStarted.Lazily, emptyList())
    fun insert(title: String, description: String) =
        viewModelScope.launch {
            if (title.isBlank() && description.isBlank()) return@launch
            repo.insert(Note(title = title.trim(), description =
                description.trim()))
        }
    fun update(id: Long, title: String, description: String) =
        viewModelScope.launch {
            if (id <= 0) return@launch
            repo.update(Note(id = id, title = title.trim(), description =
                description.trim()))
        }
    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note) }
}
