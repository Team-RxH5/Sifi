package com.anagramsoftware.sifi.util

import com.google.firebase.firestore.*
import io.reactivex.*

object RxFirestore {

    /**
     * Adds a new document to this collection with the specified data, assigning it a document ID automatically.
     *
     * @param ref  The given Collection reference.
     * @param data A Map containing the data for the new document..
     * @return a Single which emits the [DocumentReference] of the added Document.
     */
    fun addDocument( ref: CollectionReference,
                     data: Map<String, Any>): Single<DocumentReference> {
        return Single.create { emitter ->
            ref.add(data).addOnCompleteListener { task -> task.result?.let { emitter.onSuccess(it) }}.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Adds a new document to this collection with the specified POJO as contents, assigning it a document ID automatically.
     *
     * @param ref  The given Collection reference.
     * @param pojo The POJO that will be used to populate the contents of the document.
     * @return a Single which emits the [DocumentReference] of the added Document.
     */
    fun addDocument( ref: CollectionReference,
                     pojo: Any): Single<DocumentReference> {
        return Single.create { emitter ->
            ref.add(pojo).addOnCompleteListener { task -> task.result?.let { emitter.onSuccess(it) }}.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Set a new document to this collection to specified key with the specified POJO as contents.
     *
     * @param ref  The given Collection reference.
     * @param id  The given Document id.
     * @param pojo The POJO that will be used to populate the contents of the document.
     * @return a Single which emits the [DocumentReference] of the added Document.
     */
    fun setDocument( ref: CollectionReference,
                     id: String,
                     pojo: Any): Completable {
        return Completable.create { emitter ->
            ref.document(id).set(pojo).addOnCompleteListener { emitter.onComplete() }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Check availability of a specified document in this collection.
     *
     * @param ref  The given Collection reference.
     * @param id  The given Document id.
     * @return a Single which emits the [DocumentReference] of the added Document.
     */
    fun checkDocument( ref: CollectionReference,
                     id: String): Single<Boolean> {
        return Single.create { emitter ->
            ref.document(id).get().addOnCompleteListener { it ->
                it.result?.let {
                    if (it.exists())
                        emitter.onSuccess(true)
                    else
                        emitter.onSuccess(false)   
                }
            }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Reads the document referenced by this DocumentReference.
     *
     * @param ref The given Document reference.
     */
    fun getDocument(ref: DocumentReference): Maybe<DocumentSnapshot> {
        return Maybe.create { emitter ->
            ref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    emitter.onSuccess(documentSnapshot)
                } else {
                    emitter.onComplete()
                }
            }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Reads the document referenced by this DocumentReference.
     *
     * @param ref The given Document reference.
     * @param valueType The given Document type.
     */
    fun <T> getDocument(ref: DocumentReference, valueType: Class<T>): Maybe<T> {
        return Maybe.create { emitter ->
            ref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val value = documentSnapshot.toObject(valueType)
                    if (value != null)
                        emitter.onSuccess(value)
                    else
                        emitter.onError(NullPointerException())
                } else {
                    emitter.onComplete()
                }
            }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Reads the collection referenced by this DocumentReference
     *
     * @param ref The given Collection reference.
     */
    fun getCollection(ref: CollectionReference): Maybe<QuerySnapshot> {
        return Maybe.create { emitter ->
            ref.get().addOnSuccessListener { documentSnapshots ->
                if (documentSnapshots.isEmpty) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(documentSnapshots)
                }
            }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }


    /**
     * Reads the collection referenced by this DocumentReference
     *
     * @param query The given Collection query.
     */

    fun getCollection(query: Query): Maybe<QuerySnapshot> {
        return Maybe.create { emitter ->
            query.get().addOnSuccessListener { documentSnapshots ->
                if (documentSnapshots.isEmpty) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(documentSnapshots)
                }
            }.addOnFailureListener { e ->
                if (!emitter.isDisposed)
                    emitter.onError(e)
            }
        }
    }

    /**
     * Starts listening to the document referenced by this DocumentReference with the given options.
     *
     * @param ref      The given Document reference.
     * @param metadataChanges  Listen for metadata changes
     * @param strategy [BackpressureStrategy] associated to this [Flowable]
     */
    @JvmOverloads
    fun observeDocumentRef( ref: DocumentReference,
                            metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE,
                            strategy: BackpressureStrategy = BackpressureStrategy.DROP): Flowable<DocumentSnapshot> {
        return Flowable.create({ emitter ->
            val registration = ref.addSnapshotListener(metadataChanges, EventListener { documentSnapshot, e ->
                if (e != null && !emitter.isCancelled) {
                    emitter.onError(e)
                    return@EventListener
                }
                documentSnapshot?.let { emitter.onNext(it) }
            })
            emitter.setCancellable { registration.remove() }
        }, strategy)
    }

    /**
     * Starts listening to the document referenced by this Query with the given options.
     *
     * @param ref      The given Query reference.
     * @param metadataChanges  Listen for metadata changes
     * @param strategy [BackpressureStrategy] associated to this [Flowable]
     */
    @JvmOverloads
    fun observeQueryRef(ref: Query,
                        metadataChanges: MetadataChanges = MetadataChanges.EXCLUDE,
                        strategy: BackpressureStrategy = BackpressureStrategy.DROP): Flowable<QuerySnapshot> {
        return Flowable.create({ emitter ->
            val registration = ref.addSnapshotListener(metadataChanges, EventListener { querySnapshot, e ->
                if (e != null && !emitter.isCancelled) {
                    emitter.onError(e)
                    return@EventListener
                }
                if (querySnapshot != null) {
                    emitter.onNext(querySnapshot)
                }
            })
            emitter.setCancellable { registration.remove() }
        }, strategy)
    }
}