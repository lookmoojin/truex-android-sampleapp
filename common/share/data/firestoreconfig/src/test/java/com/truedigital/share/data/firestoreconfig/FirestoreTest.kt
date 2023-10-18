package com.truedigital.share.data.firestoreconfig

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.mockito.stubbing.Answer
import java.util.concurrent.Executor

open class FirestoreTest {

    protected val firestoreUtil: FirestoreUtil = mock()
    private val firestore: FirebaseFirestore = mock()
    private val firestoreCollectionReference: CollectionReference = mock()
    private val firestoreDocumentReference: DocumentReference = mock()
    protected val firestoreDocumentSnapshot: DocumentSnapshot = mock()

    fun setUpFirestoreTest() {
        whenever(firestoreUtil.getFirestore()).thenReturn(firestore)
        whenever(firestore.collection(any())).thenReturn(firestoreCollectionReference)
        whenever(firestoreCollectionReference.document(any())).thenReturn(firestoreDocumentReference)
        whenever(firestoreDocumentReference.collection(any())).thenReturn(
            firestoreCollectionReference
        )
    }

    fun getDataSuccess() {
        val answer = Answer {
            return@Answer object : Task<DocumentSnapshot>() {
                override fun isComplete(): Boolean {
                    return true
                }

                override fun isSuccessful(): Boolean {
                    return true
                }

                override fun isCanceled(): Boolean {
                    return false
                }

                override fun getResult(): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun getException(): Exception? {
                    return Exception("fail")
                }

                override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
                    p0.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Executor,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    p1.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Activity,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    p1.onSuccess(firestoreDocumentSnapshot)
                    return this
                }

                override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnFailureListener(
                    p0: Executor,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnFailureListener(
                    p0: Activity,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    return this
                }
            }
        }
        whenever(firestoreDocumentReference.get()).thenAnswer(answer)
    }

    fun getDataFail() {
        val answer = Answer {
            return@Answer object : Task<DocumentSnapshot>() {
                override fun isComplete(): Boolean {
                    return true
                }

                override fun isSuccessful(): Boolean {
                    return false
                }

                override fun isCanceled(): Boolean {
                    return false
                }

                override fun getResult(): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun <X : Throwable?> getResult(p0: Class<X>): DocumentSnapshot {
                    return firestoreDocumentSnapshot
                }

                override fun getException(): Exception? {
                    return Exception("fail")
                }

                override fun addOnSuccessListener(p0: OnSuccessListener<in DocumentSnapshot>): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Executor,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnSuccessListener(
                    p0: Activity,
                    p1: OnSuccessListener<in DocumentSnapshot>
                ): Task<DocumentSnapshot> {
                    return this
                }

                override fun addOnFailureListener(p0: OnFailureListener): Task<DocumentSnapshot> {
                    p0.onFailure(Exception("Fail"))
                    return this
                }

                override fun addOnFailureListener(
                    p0: Executor,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    p1.onFailure(Exception("Fail"))
                    return this
                }

                override fun addOnFailureListener(
                    p0: Activity,
                    p1: OnFailureListener
                ): Task<DocumentSnapshot> {
                    p1.onFailure(Exception("Fail"))
                    return this
                }
            }
        }
        whenever(firestoreDocumentReference.get()).thenAnswer(answer)
    }
}
