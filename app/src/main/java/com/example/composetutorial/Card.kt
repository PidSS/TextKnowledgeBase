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
            title = "XSS跨站脚本攻击",
            description = "跨站脚本（Cross-Site Scripting，XSS）是一种经常出现在 WEB 应用程序中的计算机安全漏洞，是由于 WEB 应用程序对用户的输入过滤不足而产生的。"
            // 替换为实际的图片资源ID
        ),
        CardData(
            title = "SSRF服务端请求伪造",
            description = "SSRF，Server-Side Request Forgery，服务端请求伪造，是一种由攻击者构造形成由服务器端发起请求的一个漏洞。"
            // 替换为实际的图片资源ID
        ),
        CardData(
            title = "RSA加密算法",
            description = "RSA 加密算法是一种非对称加密算法。在公开密钥加密和电子商业中 RSA 被广泛使用。RSA 是 1977 年由罗纳德 · 李维斯特（Ron Rivest）、阿迪 · 萨莫尔（Adi Shamir）和伦纳德 · 阿德曼…"
            // 替换为实际的图片资源ID
        )
    )
}
