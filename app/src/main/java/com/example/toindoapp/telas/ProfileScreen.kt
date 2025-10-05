package com.example.toindoapp.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold // MUDANÇA AQUI
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.toindoapp.R

private val IconBg=Color(0xfff0f1f3)
private val PrimaryText=Color(0xff1b1c1e)
private val SecondaryText=Color(0xff8a8e95)

private val MidSheet = Color(0xfff3f4f6)

@Composable
fun ProfileScreen(
    navController: NavController,
    onNotification: ()-> Unit ={},
    onCalendar: ()-> Unit ={},
    onGallery: ()-> Unit ={},
    onPlaylist: ()-> Unit ={},
    onShare: ()-> Unit ={},
    onLogout: ()-> Unit ={},

    ){
    val scroll = rememberScrollState()

    // O Scaffold foi trocado para a versão do material3
    Scaffold(
        bottomBar = { BottomMenu(navController = navController) }
    ) { innerPadding -> // O lambda agora fornece um padding que deve ser aplicado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MidSheet)
                // O padding do Scaffold é aplicado aqui no container principal
                .padding(innerPadding)
        ){
            Image(
                painter = painterResource(R.drawable.arc_pic),
                contentDescription=null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Este Box e Column agora estão dentro do conteúdo do Scaffold,
        // mas o padding não precisa ser aplicado novamente se a estrutura for de sobreposição.
        // Se eles fossem o conteúdo principal, o .padding(innerPadding) iria neles.
        // Para a sua estrutura de sobreposição com Box, está correto não aplicar o padding aqui
        // e deixar o conteúdo fluir por baixo da barra de navegação.

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 400.dp)
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color.White)
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 230.dp)
            .verticalScroll(scroll)
        ){
            Surface(shape = CircleShape,
                shadowElevation = 6.dp,
                color= Color.White,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            ){
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)

                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text="Alex Flores",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color=PrimaryText
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.height(16.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
            ) {
                MenuItemRow("Notification", R.drawable.btn_1, onNotification)
                MenuItemRow("Calendar", R.drawable.btn_2, onCalendar)
                MenuItemRow("Galllery", R.drawable.btn_3, onGallery)
                MenuItemRow("My playlist", R.drawable.btn_4, onPlaylist)
                MenuItemRow("Share", R.drawable.btn_5, onShare)
                MenuItemRow("Logout", R.drawable.btn_6, onLogout)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Preview
@Composable
private fun MenuItemRowPreview(){
    MenuItemRow(
        title = "Notifications",
        iconRes = R.drawable.btn_1,
        onClick = {}
    )
}

@Preview
@Composable
fun ProfileScreenPreview(){
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}

@Composable
private fun MenuItemRow(
    title: String,
    iconRes: Int,
    onClick:()-> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()}
            .padding(
                horizontal = 16.dp, vertical = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        Surface(
            shape = CircleShape,
            tonalElevation = 6.dp,
            color = IconBg,
            modifier = Modifier.size(50.dp)
        ) {
            Image(painter = painterResource(iconRes), contentDescription = null)
        }
        Spacer(Modifier.width(14.dp))

        Text(text = title,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis

        )
        Icon(
            painter = painterResource(R.drawable.arrow),
            contentDescription = null,
            tint = SecondaryText
        )
    }
}