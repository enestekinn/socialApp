package com.example.socialapp.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialapp.data.entities.User
import com.example.socialapp.other.Event
import com.example.socialapp.other.Resource
import com.example.socialapp.repository.main.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel()
 {
    private val _searchResults = MutableLiveData<Event<Resource<List<User>>>>()
     val searchResults : LiveData<Event<Resource<List<User>>>> = _searchResults

     fun searchUser(query: String) {
         if (query.isEmpty()) {
             return
         }

         _searchResults.postValue(Event(Resource.Loading()))
         viewModelScope.launch(dispatcher) {
             var result = repository.searchUser(query)
             _searchResults.postValue(Event(result))
         }
     }
}