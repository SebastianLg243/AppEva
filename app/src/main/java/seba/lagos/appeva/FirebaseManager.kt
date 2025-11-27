package seba.lagos.appeva

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log

object FirebaseManager {

    private val database = FirebaseDatabase.getInstance().reference

    private val usersRef = database.child("Usuario")


    fun loginUser(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            callback(false, "Error", "Por favor, complete ambos campos.")
            return
        }


        usersRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {


                        for (userSnapshot in snapshot.children) {


                            @Suppress("UNCHECKED_CAST")
                            val userData = userSnapshot.value as? HashMap<String, Any>

                            if (userData != null) {

                                val dbPassword = userData["contrasena"]?.toString()
                                val username = userData["nombre"]?.toString()
                                val userType = userData["tipo"]?.toString() ?: "Operador"

                                if (dbPassword == password && !username.isNullOrEmpty()) {

                                    callback(true, username, userType)
                                    return
                                }
                            }
                        }

                        callback(false, "Error", "Contraseña incorrecta.")
                    } else {

                        callback(false, "Error", "Usuario no encontrado.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("FIREBASE_ERROR: ${error.message}")
                    callback(false, "Error", "Error de Firebase: ${error.message}")
                }
            })
    }
    fun actualizarEstadoCircuito(estado: String) {
        val ref = FirebaseDatabase.getInstance().getReference("ControlCircuito")
        ref.child("estado").setValue(estado)
            .addOnSuccessListener {
                Log.d("Firebase", "Estado actualizado a: $estado")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al actualizar estado: ${e.message}")
            }
    }
}