package com.example.composetutorial

import RegisterRequest
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import com.example.myapp.CardData
import com.example.myapp.CardSampleData.cards
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityContent()
        }
    }
}

@Composable
fun MainActivityContent() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    ComposeTutorialTheme {
        MainScreen(
            cards = cards,
            isLoggedIn = isLoggedIn,
            onLoginStatusChanged = { isLoggedIn = it }
        )
    }
}






@Composable
fun MainScreen(cards: List<CardData>, isLoggedIn: Boolean, onLoginStatusChanged: (Boolean) -> Unit) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    // Determine if the current route is the card detail screen
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val showBottomBar = currentBackStackEntry?.destination?.route != "details/{cardId}"

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
                val cardId = backStackEntry.arguments?.getString("cardId")
                val card = cards.find { it.id == cardId }
                if (card != null) {
                    CardDetailScreen(card = card)
                }
            }
            composable("favorites") {
                FavoriteScreen(cards = cards, navController = navController)
            }
            composable("profile") {
                ProfileScreen(
                    cards = cards,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    onLoginStatusChanged = onLoginStatusChanged
                )
            }
        }
    }
}




@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var showRegisterForm by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

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
            Button(onClick = onLogin) {
                Text(text = "登录")
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
                                    // 确认注册成功后更新状态
                                    successMessage = "注册成功"
                                    errorMessage = ""  // 清空错误消息
                                    onRegister() // 通知注册成功
                                } else {
                                    errorMessage = response.message ?: "注册失败：未知错误"
                                    // 在注册失败时清空成功消息，确保只显示注册失败消息
                                    successMessage = "注册成功"
                                }
                            } catch (e: Exception) {
                                errorMessage = "注册失败：${e.message}"
                                // 在注册失败时清空成功消息，确保只显示注册失败消息
                                successMessage = ""
                            }
                        }
                    }
                }) {
                    Text(text = "确认注册")
                }

// 根据条件显示文本
                if (successMessage == "注册成功") {
                    Text(text = successMessage, color = Color.Green)
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red)
                }


            }
        }
    }
}



@Composable
fun RegisterScreen() {


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
fun HomeScreen(
    cards: List<CardData>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    isLoggedIn: Boolean,
    onLoginStatusChanged: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (selectedTab) {
            0 -> HorizontalCardList(cards = cards, navController = navController)
            1 -> FavoriteScreen(cards = cards, navController = navController)
            2 -> ProfileScreen(
                cards = cards,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                navController = navController,
                isLoggedIn = isLoggedIn,
                onLoginStatusChanged = onLoginStatusChanged
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
fun HorizontalCardList(cards: List<CardData>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .padding(top = 75.dp, start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(cards) { card ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable { navController.navigate("details/${card.id}") }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = card.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = card.description,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteScreen(cards: List<CardData>, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "收藏",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            navController.navigate("details/${card.id}")
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = card.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = card.description,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
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
    onLoginStatusChanged: (Boolean) -> Unit
) {
    if (isLoggedIn) {
        // 已登录状态下显示的内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "用户名",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "浏览记录",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cards) { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                navController.navigate("details/${card.id}")
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = card.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = card.description,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    } else {
        // 未登录状态下显示的内容
        LoginScreen(
            onLogin = { onLoginStatusChanged(true) },
            onRegister = { /* 处理注册逻辑 */ }
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
fun CardDetailScreen(card: CardData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = card.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = card.description,
            fontSize = 16.sp,
            color = Color.Black
        )
        // 在这里添加更多详细内容
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ComposeTutorialTheme {
        val navController = rememberNavController() // 创建一个模拟的 NavController
        ProfileScreen(
            cards = cards,
            selectedTab = 2,
            onTabSelected = {},
            navController = navController,
            isLoggedIn = false, // 添加登录状态参数
            onLoginStatusChanged = {} // 添加处理登录状态变化的参数
        )
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewFavoriteScreen() {
    ComposeTutorialTheme {
        val navController = rememberNavController() // 创建一个模拟的NavController
        FavoriteScreen(navController = navController, cards = cards) // 提供所需参数
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCardDetailScreen() {
    val sampleCard = CardData(
        id = "1",
        title = "SQL注入",
        description = "SQL注入是一种通过在输入字段中插入恶意SQL代码来攻击应用程序的技术。"
    )
    ComposeTutorialTheme {
        CardDetailScreen(card = sampleCard)
    }
}
