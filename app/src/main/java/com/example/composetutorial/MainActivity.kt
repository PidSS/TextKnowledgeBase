package com.example.composetutorial

import Entry
import LoginRequest
import RegisterRequest
import UserViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import com.example.myapp.CardData
import com.example.myapp.CardSampleData.cards
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val entryViewModel: EntryViewModel = viewModel()
            MainActivityContent(entryViewModel)
        }
    }
}




@Composable
fun MainActivityContent(entryViewModel: EntryViewModel) {
    val navController = rememberNavController()
    //private lateinit var userViewModel:UserViewModel
    val context = LocalContext.current
    val userViewModel = ViewModelProvider(context as ComponentActivity).get(UserViewModel::class.java)
    val isLoggedIn by userViewModel.isLoggedIn.observeAsState(initial = false)
    val username by userViewModel.username.observeAsState(initial = "")
    ComposeTutorialTheme {
        MainScreen(
            cards = cards,
            isLoggedIn = isLoggedIn,
            onLoginStatusChanged = { isLoggedIn ->
                if (isLoggedIn) {
                    //val username = username
                    userViewModel.login(username)
                } else {
                    userViewModel.logout()
                }
            },
            userViewModel = userViewModel,
            entryViewModel = entryViewModel
        )
    }
}









@Composable
fun MainScreen(
    cards: List<CardData>,
    isLoggedIn: Boolean,
    onLoginStatusChanged: (Boolean) -> Unit,
    userViewModel: UserViewModel, // 接收 UserViewModel 实例
    entryViewModel: EntryViewModel
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    var showBottomBar by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { SearchBar() },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "profile",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                showBottomBar = true
                HomeScreen(
                    cards = cards,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    onLoginStatusChanged = onLoginStatusChanged
                )
            }
            composable("details/{cardId}") { backStackEntry ->
                showBottomBar = false
                val cardId = backStackEntry.arguments?.getString("cardId")?.toInt()
                val entries by entryViewModel.entries.observeAsState(emptyList())
                val entry = entries.find { it.id == cardId }

                if (entry != null) {
                    CardDetailScreen(entry = entry, entryViewModel = entryViewModel)
                }
            }
            composable("favorites") {
                showBottomBar = true
                val entries by entryViewModel.entries.observeAsState(emptyList())
                FavoriteScreen(entries = entries, navController = navController)
            }
            composable("profile") {
                showBottomBar = true
                val entries by entryViewModel.entries.observeAsState(emptyList())
                ProfileScreen(
                    cards = cards,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    onLoginStatusChanged = onLoginStatusChanged,
                    userViewModel = userViewModel,
                    entries = entries
                )
            }
        }
    }
}




@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    navController: NavHostController,
    userViewModel: UserViewModel, // 接收 UserViewModel 实例
    onLoginStatusChanged: (Boolean) -> Unit
) {
    var showRegisterForm by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var navigateToHome by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    if (navigateToHome) {
        LaunchedEffect(Unit) {
            delay(2000)
            navController.navigate("home")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "请登录或注册", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (!showRegisterForm) {
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = username,
                onValueChange = { newUsername -> username = newUsername },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { newText -> password = newText },
                label = { Text("密码") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                coroutineScope.launch {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "您未输入用户名或密码"
                        successMessage = ""
                    } else {
                        try {
                            val request = LoginRequest(username, password)
                            println("登录请求：$request")
                            val response = RetrofitClient.instance.login(request)
                            println("登录响应：$response")

                            if (response.token.isNotEmpty()) {
                                val token = response.token
                                saveToken(context, token)
                                successMessage = "登录成功，令牌: $token"
                                delay(1000)
                                errorMessage = ""
                                println("登录成功，令牌: $token")
                                userViewModel.login(username) // 调用 UserViewModel 的 login 方法
                                println("登录成功，用户名为：$username")
                                onLoginStatusChanged(true) // 更新登录状态
                                navigateToHome = true
                            } else {
                                errorMessage = "登录失败，未返回有效响应"
                                successMessage = ""
                                println("登录失败，未返回有效响应")
                            }
                        } catch (e: Exception) {
                            errorMessage = "登录失败: ${e.message}"
                            successMessage = ""
                            println("登录失败: ${e.message}")
                        }
                    }
                }
            }) {
                Text("登录")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showRegisterForm = true }) {
                Text(text = "注册")
            }
        }

        if (showRegisterForm) {
            // 注册表单
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = username,
                    onValueChange = { newUsername -> username = newUsername },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { newText -> password = newText },
                    label = { Text("密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { newText -> confirmPassword = newText },
                    label = { Text("确认密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (password == confirmPassword) {
                        coroutineScope.launch {
                            try {
                                val request = RegisterRequest(username, password)
                                val response = RetrofitClient.instance.register(request)
                                if (response.success) {
                                    successMessage = "注册成功"
                                    errorMessage = ""
                                    onRegister()
                                    navController.navigate("home")
                                } else {
                                    errorMessage = response.message ?: ""
                                    successMessage = "注册成功"
                                    onRegister()
                                    navController.navigate("home")
                                }
                            } catch (e: Exception) {
                                errorMessage = "注册失败：${e.message}"
                                successMessage = ""
                            }
                        }
                    } else {
                        errorMessage = "密码和确认密码不一致"
                        successMessage = ""
                    }
                }) {
                    Text(text = "确认注册")
                }

                if (successMessage.isNotEmpty()) {
                    Text(text = successMessage, color = Color.Green)
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
                }
            }
        }

        if (successMessage.isNotEmpty()) {
            Text(text = successMessage, color = Color.Green)
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}




@Composable
fun HomeScreen(
    cards: List<CardData>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    isLoggedIn: Boolean,
    onLoginStatusChanged: (Boolean) -> Unit
) {
    val entryViewModel: EntryViewModel = viewModel()
    val entries by entryViewModel.entries.observeAsState(emptyList())
    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> HorizontalCardList(entries = entries, navController = navController)
            1 -> FavoriteScreen(entries = entries, navController = navController)
            2 -> ProfileScreen(
                cards = cards,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                navController = navController,
                isLoggedIn = isLoggedIn,
                onLoginStatusChanged = onLoginStatusChanged,
                userViewModel= UserViewModel(),
                entries=entries
            )
        }
    }
}



@Composable
fun SearchBar() {
    // 创建一个状态来存储输入框的值
    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = { newText ->
            searchText = newText // 更新输入框的值
        },
        placeholder = { Text("搜索") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}


@Composable
fun HorizontalCardList(entries: List<Entry>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(entries) { entry ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { navController.navigate("details/${entry.id}") }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = entry.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.introduction.take(40).let {
                            if (entry.introduction.length > 40) "$it..." else it
                        },
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(entries: List<Entry>, navController: NavHostController) {
    val filteredEntries = entries.filter { it.isFavorite }

    if (filteredEntries.isEmpty()) {
        println("favoritescreen:No favorite entries found")
    }

    // 打印收藏状态
    println("收藏状态变化:")
    filteredEntries.forEach { entry ->
        println("favoritescreen:Entry ID: ${entry.id}, isFavorite: ${entry.isFavorite}")
    }

    LazyColumn(
        modifier = Modifier
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(filteredEntries) { entry ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { navController.navigate("details/${entry.id}") }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = entry.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.introduction.take(40).let {
                            if (entry.introduction.length > 40) "$it..." else it
                        },
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}




@Composable
fun ProfileScreen(
    cards: List<CardData>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    isLoggedIn: Boolean,
    onLoginStatusChanged: (Boolean) -> Unit,
    entries: List<Entry>,
    userViewModel: UserViewModel // 接收 UserViewModel 实例
) {
    //val isLoggedIn by userViewModel.isLoggedIn.observeAsState(initial = false)
    val context = LocalContext.current
    val viewModel = ViewModelProvider(context as ComponentActivity).get(UserViewModel::class.java)
    val username by viewModel.username.observeAsState(initial = "")

    if (isLoggedIn) {
        // 已登录状态下显示的内容
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "用户名: $username",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            if (false) {
                Text(
                    text = "用户名为空",
                    color = Color.Red
                )
            } else {
                Text(
                    text = "用户名不为空，用户名为: $username",
                    color = Color.Green
                )
            }
            Text(
                text = "浏览历史",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 75.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(entries) { entry ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable { navController.navigate("details/${entry.id}") }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = entry.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = entry.introduction.take(40).let {
                                    if (entry.introduction.length > 40) "$it..." else it
                                },
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }  else {
        // 未登录状态下显示的内容
        LoginScreen(
            navController = navController,
            onLogin = { onLoginStatusChanged(true) },
            onRegister = { onLoginStatusChanged(true) },
            userViewModel = userViewModel ,// 传入 UserViewModel 实例
            onLoginStatusChanged = onLoginStatusChanged
        )
    }
}







@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "发现"
                )
            },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "收藏"
                )
            },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "我的"
                )
            },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
    }
}


@Composable
fun CardDetailScreen(entry: Entry,entryViewModel: EntryViewModel) {
    val entries by entryViewModel.entries.observeAsState(emptyList())
    val entry = entries.find { it.id == entry.id } ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = entry.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = entry.introduction,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = entry.content,
            fontSize = 16.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(onClick = { entryViewModel.toggleFavorite(entry) }) {
            if (entry.isFavorite) {
                Icon(Icons.Default.Star, contentDescription = "Unfavorite", tint = Color.Blue)
            } else {
                Icon(Icons.Default.StarBorder, contentDescription = "Favorite", tint = Color.Gray)
            }
        }
    }
}







