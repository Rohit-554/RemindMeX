package io.jadu.remindmex.remindMe.data.repositoryImpl


import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import io.jadu.remindmex.remindMe.data.models.Reminder
import io.jadu.remindmex.remindMe.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class ReminderRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ReminderRepository {

    private val remindersCollection = firestore.collection("reminders")
    private val storageRef = storage.reference.child("reminder_images")

    override suspend fun addReminder(reminder: Reminder): Result<String> {
        return try {
            val docRef = remindersCollection.document()
            val reminderWithId = reminder.copy(id = docRef.id)
            docRef.set(reminderWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReminders(): Flow<List<Reminder>> {
        return remindersCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Reminder>()
                }
            }
            .catch { e ->
                emit(emptyList())
            }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            Log.d(
                "ReminderRepositoryImpl",
                "Updating reminder: ${reminder.id}, title: ${reminder.isCompleted}"
            )
            remindersCollection.document(reminder.id).set(reminder).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d(
                "ReminderRepositoryImpl",
                "Error updating reminder: ${reminder.id}, error: ${e.message}"
            )
            Result.failure(e)
        }
    }

    override suspend fun deleteReminder(id: String): Result<Unit> {
        return try {
            remindersCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(uri: Uri): Result<String> {
        val imageRef = storageRef.child("${System.currentTimeMillis()}.jpg")

        return try {
            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: CancellationException) {
            Log.w("ReminderRepositoryImpl", "Upload cancelled", e)
            throw e // Always rethrow to allow coroutine to cancel properly
        } catch (e: Exception) {
            Log.e("ReminderRepositoryImpl", "Upload failed", e)
            Result.failure(e)
        }
    }

}