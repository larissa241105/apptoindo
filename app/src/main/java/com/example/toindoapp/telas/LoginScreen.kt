package com.example.toindoapp.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toindoapp.R

@Composable

fun LoginScreen(
    onEmailClick:()->Unit={},
    onGoogleClick:()->Unit={},
    onSignupClick:()->Unit={},
){
    val emailColor = Color(0xffDF4A1B)
    val googleColor = Color(0xff9C27b0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
            .navigationBarsPadding()
    ){

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(Modifier.height(24.dp))

                Image(
                    painter = painterResource(id = R.drawable.pic_2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .padding(top=8.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Login",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=24.dp, bottom = 36.dp)

                )

                LoadingIconButton(text = "Continue com Email",
                    iconRes = R.drawable.email,
                    container = emailColor,
                    onClick = onEmailClick
                    )
                Spacer(Modifier.height(24.dp))

                LoadingIconButton(
                    text = "Continue com Google",
                    iconRes = R.drawable.google,
                    container = googleColor,
                    onClick = onGoogleClick
                )
                Spacer(Modifier.height(24.dp))
                Spacer(Modifier.weight(1f))
            }

        val annotatedText = buildAnnotatedString {
            append("Novo usuário?")

            // Anote o texto "Cadastre-se" para que seja clicável
            pushStringAnnotation(tag = "signup", annotation = "signup_link")
            withStyle(style = SpanStyle(
                color = Color(0xffff6a2e),
                fontWeight = FontWeight.Bold)
            ) {
                append(" Cadastre-se")
            }
            pop()
        }

        Text(
            text = annotatedText,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clickable {
                  onSignupClick()
                }
        )
        }
    }


@Composable
private fun LoadingIconButton(
    text:String,
    iconRes:Int,
    container:Color,
    onClick:()->Unit
)
{
    Button(onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Icon(painter = painterResource(iconRes), contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(text=text,
                fontSize = 18.sp,
                )
        }
    }
}






