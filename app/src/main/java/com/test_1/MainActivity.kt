@file:OptIn(ExperimentalMaterial3Api::class)

package com.test_1
import HomeViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(applicationContext)
                return UserViewModel(db.userDao() , db.taskDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyApp(navController = navController, userViewModel = userViewModel)
        }
    }
}

@Composable
fun MyApp(navController: NavHostController, userViewModel: UserViewModel) {
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { user ->
                    currentUser = user
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable("signup") {
            SignUpScreen(
                userViewModel = userViewModel,
                onSignUpSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable("home") {
            currentUser?.let { user ->
                HomeScreen(user = user, userViewModel = userViewModel, onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                })
            }
        }
    }
}

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: (User) -> Unit,
    navController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) } // State to toggle password visibility

    val buttonScale by animateFloatAsState(
        targetValue = if (email.isNotEmpty() && password.isNotEmpty()) 1.1f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color(0xFFECEFF1)),  // Light background color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF1E88E5))  // Accent color
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color(0xFFFFB6C1),
                cursorColor = Color.Black,
                errorCursorColor = Color.Red,
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField with eye icon to show/hide password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painterResource(id = if (showPassword) R.drawable.hide else R.drawable.show),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Show/hide password
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color(0xFFFFB6C1),
                cursorColor = Color.Black,
                errorCursorColor = Color.Red,
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    userViewModel.loginUser(email, password) { user ->
                        if (user != null) {
                            onLoginSuccess(user)
                        } else {
                            errorMessage = "Login failed. Please check your credentials."
                        }
                    }
                } else {
                    errorMessage = "Please fill in all fields."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp)
                .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
            contentPadding = PaddingValues(vertical = 12.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
        ) {
            Text("Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Don't have an account? Sign Up")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun SignUpScreen(
    userViewModel: UserViewModel,
    onSignUpSuccess: () -> Unit,
    navController: NavHostController
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) } // State to toggle password visibility
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val buttonScale by animateFloatAsState(
        targetValue = if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) 1.1f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color(0xFFECEFF1)),  // Light background color
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF1E88E5))  // Accent color
        Spacer(modifier = Modifier.height(32.dp))

        // Username TextField
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .background(Color.White),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color(0xFFFFB6C1),
                cursorColor = Color.Black,
                errorCursorColor = Color.Red,
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color(0xFFFFB6C1),
                cursorColor = Color.Black,
                errorCursorColor = Color.Red,
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField with eye icon to show/hide password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painterResource(id = if (showPassword) R.drawable.hide else R.drawable.show),
                        contentDescription = if (showPassword) "Hide password" else "Show password",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Show/hide password
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
                errorTextColor = Color.Red,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color(0xFFFFB6C1),
                cursorColor = Color.Black,
                errorCursorColor = Color.Red,
                focusedIndicatorColor = Color(0xFF1E88E5),
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val user = User(username = username, email = email, password = password)
                    userViewModel.registerUser(user) { success ->
                        if (success) {
                            onSignUpSuccess()
                        } else {
                            errorMessage = "Sign up failed. Please try again."
                        }
                    }
                } else {
                    errorMessage = "Please fill in all fields."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp)
                .graphicsLayer(scaleX = buttonScale, scaleY = buttonScale),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
            contentPadding = PaddingValues(vertical = 12.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Go to Login")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun HomeScreen(
    user: User,
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val homeViewModel: HomeViewModel = viewModel()
    val showDialog by remember { homeViewModel.showDialog }

    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.getTasksForUser(user.id) {
            tasks = it
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Welcome ${user.username}!", style = MaterialTheme.typography.headlineMedium, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            painter = painterResource(id = R.drawable.log_out),  // Replace with your drawable resource
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1E88E5), titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFECEFF1)) // Light background color
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))

                // Display tasks in cards
                tasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onDeleteTask = {
                            userViewModel.deleteTask(task) {
                                userViewModel.getTasksForUser(user.id) {
                                    tasks = it
                                }
                            }
                        },
                        onEditTask = {
                            taskToEdit = it
                            homeViewModel.setDialogState(true)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Floating Action Button
            FloatingActionButton(
                onClick = {
                    taskToEdit = null // Reset task to edit when adding a new task
                    homeViewModel.toggleDialog()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF1E88E5)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }

        // Dialog for adding/editing a task
        if (showDialog) {
            TaskDialog(
                initialTask = taskToEdit?.taskName ?: "",
                onDismissRequest = { homeViewModel.setDialogState(false) },
                onAddTask = { name ->
                    if (name.isNotEmpty()) {
                        if (taskToEdit == null) {
                            // Add new task
                            val newTask = Task(userId = user.id, taskName = name)
                            userViewModel.addTask(newTask) {
                                if (it) {
                                    userViewModel.getTasksForUser(user.id) {
                                        tasks = it
                                    }
                                }
                            }
                        } else {
                            // Edit existing task
                            val updatedTask = taskToEdit!!.copy(taskName = name)
                            userViewModel.updateTask(updatedTask) {
                                userViewModel.getTasksForUser(user.id) {
                                    tasks = it
                                }
                            }
                        }
                        homeViewModel.setDialogState(false)
                    }
                }
            )
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDeleteTask: () -> Unit,
    onEditTask: (Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.taskName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { onEditTask(task) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Task", tint = Color(0xFF1E88E5))
            }
            IconButton(onClick = onDeleteTask) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Task", tint = Color(0xFFDD474C))
            }
        }
    }
}
@Composable
fun TaskDialog(
    initialTask: String,
    onDismissRequest: () -> Unit,
    onAddTask: (String) -> Unit
) {
    var taskName by remember { mutableStateOf(initialTask) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (initialTask.isEmpty()) "Add New Task" else "Edit Task", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1E88E5)) },
        text = {
            Column {
                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFF1E88E5),
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAddTask(taskName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(if (initialTask.isEmpty()) "Add" else "Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )
}

