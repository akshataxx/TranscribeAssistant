import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences

// Set up a Preferences DataStore named "auth_prefs" on Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")