package com.example.composetutorial

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                MainScreen(cards = cards)
            }
        }
    }
}

@Composable
fun MainScreen(cards: List<CardData>) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    var isLoggedIn by remember { mutableStateOf(false) } // 添加登录状态管理

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
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    cards = cards,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    navController = navController,
                    isLoggedIn = isLoggedIn, // 传递登录状态
                    onLoginStatusChanged = { isLoggedIn = it } // 处理登录状态变化
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
                    isLoggedIn = isLoggedIn, // 传递登录状态
                    onLoginStatusChanged = { isLoggedIn = it } // 处理登录状态变化
                )
            }
        }
    }
}


@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var showRegisterForm by remember { mutableStateOf(false) } // 添加状态变量控制是否显示注册表单

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "请登录或注册", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (!showRegisterForm) {
            // 当不显示注册表单时显示登录和注册按钮
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogin) {
                Text(text = "登录")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showRegisterForm = true }) { // 点击注册按钮显示注册表单
                Text(text = "注册")
            }
        }

        if (showRegisterForm) {
            // 显示注册表单
            Spacer(modifier = Modifier.height(16.dp))
            // 添加注册表单内容，如用户名、密码、确认密码等
            TextField(value = "", onValueChange = { /* 处理用户名输入 */ }, label = { Text("用户名") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "", onValueChange = { /* 处理密码输入 */ }, label = { Text("密码") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = "", onValueChange = { /* 处理确认密码输入 */ }, label = { Text("确认密码") })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* 处理注册逻辑 */ }) {
                Text(text = "确认注册")
            }
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
    TextField(
        value = "",
        onValueChange = {},
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
fun PreviewMainScreen() {
    ComposeTutorialTheme {
        MainScreen(cards = cards)
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
