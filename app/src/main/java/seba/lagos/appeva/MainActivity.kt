package seba.lagos.appeva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import seba.lagos.appeva.ui.theme.AppEvaTheme
import android.util.Log
import com.google.firebase.database.*
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppEvaTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }


        composable("register") { RegisterScreen(navController) }
        composable("recovery") { RecoveryScreen(navController) }


        composable("options/{userName}/{userType}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            val userType = backStackEntry.arguments?.getString("userType") ?: "Operador"

            OptionsScreen2(navController, userName, userType)
        }


        composable("acciones/{userName}/{userType}") { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "Operador"
            VerAccionesScreen(navController, userType)
        }
        composable("perfil/{userName}/{userType}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            val userType = backStackEntry.arguments?.getString("userType") ?: "Operador"
            VerPerfilScreen(navController, userName, userType)
        }


        composable("manipular/{userName}/{userType}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            val userType = backStackEntry.arguments?.getString("userType") ?: "Operador"
            ManipularCircuitoScreen(navController, userName, userType)
        }
        composable("estadisticas/{userName}/{userType}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Usuario"
            val userType = backStackEntry.arguments?.getString("userType") ?: "Operador"
            VerEstadisticasScreen(navController, userName, userType)
        }
    }
}


@Composable
fun LoginScreen(navController: NavHostController) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.background_login),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(36.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passwordState = remember { mutableStateOf(TextFieldValue()) }
            var loginMessage by remember { mutableStateOf("") }

            Spacer(modifier = Modifier.weight(1f))

            TextField(
                label = { Text("Correo") },
                placeholder = { Text("Ingrese su correo") },
                value = emailState.value,
                onValueChange = { emailState.value = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            TextField(
                label = { Text("Contraseña") },
                placeholder = { Text("Ingrese su contraseña") },
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (loginMessage.isNotEmpty()) {
                Text(loginMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            Button(
                onClick = {
                    val email = emailState.value.text.trim().lowercase()
                    val pass = passwordState.value.text.trim()
                    loginMessage = ""

                    FirebaseManager.loginUser(email, pass) { success, nombre, tipo ->
                        if (success) {
                            navController.navigate("options/$nombre/$tipo")
                        } else {
                            loginMessage = tipo
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Ingresar", color = Color.White, fontSize = 18.sp)
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { navController.navigate("recovery") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Color Verde
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear Cuenta", color = Color.White, fontSize = 18.sp)
            }


            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun OptionsScreen2(navController: NavHostController, userName: String, userType: String) {
    val darkRed = Color(0xFFC62828)

    val accentColor = if (userType == "Administrador") Color(0xFFE53935) else Color(0xFF00B0FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Hola, ${userName.uppercase()}!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 40.dp, bottom = 4.dp)
        )
        Text(
            text = "Rol: $userType",
            color = accentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 30.dp)
        )


        if (userType == "Administrador") {
            MenuButton(
                text = "Manipular Circuito",
                color = Color(0xFFFFA000), // Naranja
                onClick = { navController.navigate("manipular/$userName/$userType") }
            )
        }


        MenuButton(
            text = "Ver Monitoreo",
            color = Color(0xFF00B0FF), // Azul
            onClick = { navController.navigate("acciones/$userName/$userType") }
        )


        MenuButton(
            text = "Ver Estadísticas",
            color = Color(0xFF4CAF50), // Verde
            onClick = { navController.navigate("estadisticas/$userName/$userType") }
        )


        MenuButton(
            text = "Ver Perfil",
            color = Color(0xFF673AB7), // Púrpura
            onClick = { navController.navigate("perfil/$userName/$userType") }
        )

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = {

                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = darkRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Cerrar Sesión", color = Color.White, fontSize = 18.sp)
        }
    }
}


@Composable
fun RegisterScreen(navController: NavHostController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var registerMessage by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Red) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "CREAR NUEVA CUENTA",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 60.dp, bottom = 30.dp)
        )

        TextField(
            label = { Text("Correo Electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            value = emailState.value,
            onValueChange = { emailState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        TextField(
            label = { Text("Contraseña (mín. 6 caracteres)") },
            placeholder = { Text("••••••••") },
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (registerMessage.isNotEmpty()) {
            Text(
                registerMessage,
                color = messageColor,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                val email = emailState.value.text.trim()
                val password = passwordState.value.text.trim()

                if (email.isEmpty() || password.isEmpty() || !email.contains("@")) {
                    registerMessage = "Ingrese un correo y una contraseña válida."
                    messageColor = Color.Red
                } else if (password.length < 6) {
                    registerMessage = "La contraseña debe tener al menos 6 caracteres."
                    messageColor = Color.Red
                } else {
                    // Simulación de éxito:
                    registerMessage = "Cuenta creada con éxito (simulado). Ahora inicie sesión."
                    messageColor = Color(0xFF4CAF50)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Registrar Cuenta", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161))
        ) {
            Text("Volver al Login", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun RecoveryScreen(navController: NavHostController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    var recoveryMessage by remember { mutableStateOf("") }
    var messageColor by remember { mutableStateOf(Color.Red) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "RECUPERAR CONTRASEÑA",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 60.dp, bottom = 30.dp)
        )

        Text(
            "Ingrese su correo electrónico. Le enviaremos un enlace para restablecer su contraseña.",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        TextField(
            label = { Text("Correo Electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            value = emailState.value,
            onValueChange = { emailState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (recoveryMessage.isNotEmpty()) {
            Text(
                recoveryMessage,
                color = messageColor,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                val email = emailState.value.text.trim()

                if (!email.contains("@") || email.isEmpty()) {
                    recoveryMessage = "Por favor, ingrese un correo electrónico válido."
                    messageColor = Color.Red
                } else {

                    recoveryMessage = "Enlace de restablecimiento enviado a $email (simulado)."
                    messageColor = Color(0xFF00B0FF)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)), // Color Naranja
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Enviar Enlace de Recuperación", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF616161))
        ) {
            Text("Volver al Login", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun ManipularCircuitoScreen(navController: NavHostController, userName: String, userType: String) {
    var estado by remember { mutableStateOf("Desactivado") }
    val darkRed = Color(0xFFB71C1C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Manipular Circuito",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 60.dp, bottom = 5.dp)
        )
        Text(
            text = "Usuario: ${userName.uppercase()} (${userType})",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 60.dp)
        )

        Text(
            text = "Estado Actual:",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = estado.uppercase(),
            color = if (estado == "Activo") Color(0xFF4CAF50) else darkRed,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 80.dp)
        )

        Button(
            onClick = {
                estado = "Activo"
                FirebaseManager.actualizarEstadoCircuito("Activo")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .size(200.dp)
                .padding(20.dp)
        ) {
            Text("ACTIVAR", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = {
                estado = "Desactivado"
                FirebaseManager.actualizarEstadoCircuito("Desactivado")
            },
            colors = ButtonDefaults.buttonColors(containerColor = darkRed),
            modifier = Modifier
                .size(200.dp)
                .padding(20.dp)
        ) {
            Text("DESACTIVAR", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver", color = Color.White, fontSize = 18.sp)
        }
    }
}
@Composable
fun VerEstadisticasScreen(navController: NavHostController, userName: String, userType: String) {
    val data = mapOf(
        "Activaciones" to 9,
        "Desactivaciones" to 8,
        "Obstáculos Detectados" to 17,
        "Distancia Total (cm)" to 46
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Estadísticas",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 60.dp, bottom = 5.dp)
        )
        Text(
            text = "Usuario: ${userName.uppercase()} (${userType})",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        StatisticsCard(
            label = "Activaciones",
            value = data["Activaciones"].toString(),
            color = Color(0xFF4CAF50)
        )
        StatisticsCard(
            label = "Desactivaciones",
            value = data["Desactivaciones"].toString(),
            color = Color(0xFFE53935)
        )


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Rojo oscuro
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver", color = Color.White, fontSize = 18.sp)
        }
    }
}


@Composable
fun VerAccionesScreen(navController: NavHostController, userType: String) {
    var temperatura by remember { mutableStateOf("-- °C") }
    var humedad by remember { mutableStateOf("-- %") }
    var ultimaActualizacion by remember { mutableStateOf("--") }

    val accentColor = if (userType == "Administrador") Color(0xFFE53935) else Color(0xFF00B0FF)
    val darkBg = Color(0xFF1A1A1A)

    LaunchedEffect(Unit) {
        leerUltimoDatoDesdeFirebase { dato ->
            if (dato != null) {
                temperatura = "${dato.temperatura} °C"
                humedad = "${dato.humedad} %"
                ultimaActualizacion = "TS: ${dato.ts}"
            } else {
                temperatura = "-- °C"
                humedad = "-- %"
                ultimaActualizacion = "--"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            "MONITOREO ESTACIÓN METEOROLÓGICA",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            "Datos en tiempo real (Rol: $userType)",
            color = Color.Gray,
            fontSize = 14.sp
        )

        // Tarjetas reales según tu Firebase
        DataSensorCard("Temperatura", temperatura, Color.Red, "🌡️")
        DataSensorCard("Humedad", humedad, Color(0xFF00B0FF), "💧")

        Spacer(modifier = Modifier.height(30.dp))
        Text("Actualizado: $ultimaActualizacion", color = Color.LightGray, fontSize = 12.sp)

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Volver al Menú", color = Color.White, fontSize = 18.sp)
        }
    }
}
@Composable
fun VerPerfilScreen(navController: NavHostController, userName: String, userType: String) {

    val emailSimulado = when (userName) {
        "sebalagos" -> "cabrerasebastian114@gmail.com"
        "ncabrera" -> "nachocabrera912@gmail.com"
        "admin" -> "admin@empresa.com"
        else -> "usuario@default.com"
    }

    val nameSimulado = when (userName) {
        "sebalagos" -> "Sebastián Lagos"
        "ncabrera" -> "Ignacio Cabrera"
        "admin" -> "Administrador Principal"
        else -> "Usuario Genérico"
    }

    val profileColor = if (userType == "Administrador") Color(0xFFE53935) else Color(0xFF4CAF50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(profileColor)
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.first().uppercase(),
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userType.uppercase(),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(profileColor.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ProfileInfoRow(label = "Nombre Completo:", value = nameSimulado)
                Divider(color = Color.DarkGray, thickness = 1.dp)
                ProfileInfoRow(label = "Usuario:", value = userName)
                Divider(color = Color.DarkGray, thickness = 1.dp)
                ProfileInfoRow(label = "Correo Electrónico:", value = emailSimulado)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (userType == "Administrador") "Acceso Total al Control y Monitoreo." else "Acceso de Operación (Solo Monitoreo).",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = profileColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Volver al Menú", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DataSensorCard(label: String, value: String, unitColor: Color, icon: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                Column {
                    Text(text = label, color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = value,
                        color = unitColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Yellow)
            )
        }
    }
}

@Composable
fun StatisticsCard(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Color.White, fontSize = 18.sp)
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp)
        Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

data class DatosMeteorologicos(
    val temperatura: Double = 0.0,
    val humedad: Double = 0.0,
    val ts: Long = 0
)

fun leerDatosDesdeFirebase(onDatosRecibidos: (DatosMeteorologicos?) -> Unit) {
    val ref = FirebaseDatabase.getInstance().getReference("DatosMeteorologicos")
    ref.limitToLast(1).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var dato: DatosMeteorologicos? = null
            for (datoSnapshot in snapshot.children) {
                dato = datoSnapshot.getValue(DatosMeteorologicos::class.java)
            }
            onDatosRecibidos(dato)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error al leer datos: ${error.message}")
        }
    })
}


val URL_ESP32 = "http://10.58.126.190/guardar" // Cambia a la IP de tu ESP32

fun enviarEstadoAlESP32(encender: Boolean) {
    val estado = if (encender) "Activo" else "Desactivado"
    val numero = if (encender) 1 else 0
    val json = """
        {
          "estado": "$estado",
          "numero": $numero
        }
    """.trimIndent()

    Thread {
        try {
            val conn = URL(URL_ESP32).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.outputStream.write(json.toByteArray())
            val responseCode = conn.responseCode
            Log.d("ESP32", "Respuesta: $responseCode")
            conn.disconnect()
        } catch (e: Exception) {
            Log.e("ESP32", "Error al enviar datos: ${e.message}")
        }
    }.start()
}


fun leerUltimoDatoDesdeFirebase(onDatos: (DatosMeteorologicos?) -> Unit) {
    val ref = FirebaseDatabase.getInstance().getReference("Datos")

    ref.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (nodo in snapshot.children) {
                val temp = nodo.child("temperatura").getValue(Double::class.java) ?: 0.0
                val hum = nodo.child("humedad").getValue(Double::class.java) ?: 0.0
                val ts = nodo.child("ts").getValue(Long::class.java) ?: 0L

                onDatos(DatosMeteorologicos(temp, hum, ts))
                return
            }
            onDatos(null)
        }

        override fun onCancelled(error: DatabaseError) {
            onDatos(null)
        }
    })
}