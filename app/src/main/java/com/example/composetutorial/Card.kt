// Card.kt
package com.example.myapp

import java.util.UUID

// 定义CardData数据类
data class CardData(
    val id: String = UUID.randomUUID().toString(), // 自动生成唯一ID
    val title: String,
    val description: String
)

// 创建CardSampleData对象，提供一些示例数据
object CardSampleData {
    val cards = listOf(
        CardData(
            title = "SQL注入",
            description = "SQL注入是一种将SQL代码插入或添加到应用用户的输入参数中，之后再将这些参数传递给后台的SQL服务器加以解析并执行的攻击。",

        ),
        CardData(
            title = "XSS攻击",
            description = "XSS攻击是指攻击者在网页中注入恶意脚本，当用户浏览该网页时，恶意脚本会在用户的浏览器中执行。"
            // 替换为实际的图片资源ID
        ),
        CardData(
            title = "CSRF攻击",
            description = "CSRF攻击是指攻击者通过伪造用户请求，使用户在不知情的情况下执行一些恶意操作。"
            // 替换为实际的图片资源ID
        ),
        CardData(
            title = "钓鱼攻击",
            description = "钓鱼攻击是指攻击者通过伪装成可信任实体，诱骗用户提供敏感信息，如用户名、密码等。"
            // 替换为实际的图片资源ID
        )
    )
}
