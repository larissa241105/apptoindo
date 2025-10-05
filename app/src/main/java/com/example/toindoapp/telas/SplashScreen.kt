package com.example.toindoapp.telas


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toindoapp.R

//private val Arrangement.CenterVertically: Arrangement.Horizontal
private val FutureRed = Color(color=0xfff33f4c)
@Composable
fun SplashScreen(
    onLogin: () ->  Unit =  {},
    onSignUp: () -> Unit = {}
){
    Box(
        modifier = Modifier.
        fillMaxSize()
    ){
        Column(modifier = Modifier.
        fillMaxSize()
            .padding(horizontal = 24.dp)
        ){
            Spacer(Modifier.height(height = 12.dp))
            Image(painter = painterResource(id = R.drawable.pic_1), contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 360.dp),
                contentScale = ContentScale.Fit)
            Spacer(Modifier.height(height = 12.dp))
            Text(
                text="Bem vindo ao",
                fontSize= 55.sp,
                fontFamily = FontFamily(Font(resId= R.font.stash)),
                fontStyle = FontStyle.Italic,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text="Tô indo",
                fontSize= 84.sp,
                fontFamily = FontFamily(Font(resId= R.font.stash)),
                fontStyle = FontStyle.Italic,
                color = FutureRed,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=4.dp)
            )
            Spacer(Modifier.height(height = 12.dp))
        }

        Box(modifier= Modifier
            .fillMaxWidth()
            .align ( Alignment.BottomCenter ))
        {
            Image(
                painter=painterResource(R.drawable.wavy_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    onClick = onLogin,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = FutureRed
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(width = 14.dp))
                OutlinedButton(
                    onClick = onSignUp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.
                    weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Text(
                        text = "Cadastre-se",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun SplashPreview(){
    MaterialTheme{ SplashScreen()}
}