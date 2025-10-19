package com.example.toindoapp.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.toindoapp.R

private val PrimaryRed= Color(0xfffDF4A1B)
private val Black = Color(0xff000000)
private val Gray = Color(0xff888888)


enum class AuthMode{SignIn, Signup}

@Composable
fun SignInScreen(
    mode:AuthMode=AuthMode.SignIn,
    onPrimary:(String, String, String)->Unit={_,_,_->},
    onForgotPassword:()->Unit={},
    onForgotNumber:()->Unit={},
    onSwitch:()->Unit={},
){
    val isSignUp = mode == AuthMode.Signup
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}
    var confirm by remember {mutableStateOf("")}
    var showPassword by remember {mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(R.drawable.pic_3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Spacer(Modifier.height(8.dp))

        if(isSignUp) {
            Text(
                text = "Cadastro",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if(isSignUp){
            TextField(
                value = name,
                onValueChange = {name = it},
                singleLine = true,
                label = {Text("Nome")},
                leadingIcon = { Icon(painterResource(R.drawable.ic_person_outlined
                ), null   ) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Gray,
                    focusedTextColor = Color.Black, // Adicione esta linha
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Gray, // Cor do label quando o campo está focado
                    unfocusedLabelColor = Color.Gray

                )
            )
        }
       TextField(
           value = email,
           onValueChange = {email = it},
           singleLine = true,
           label = {Text("Email")},
           leadingIcon = { Icon(painterResource(R.drawable.phone_storake), null   ) },
           keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
           modifier = Modifier
               .fillMaxWidth(),
           colors = TextFieldDefaults.colors(
               focusedContainerColor = Color.Transparent,
               unfocusedContainerColor = Color.Transparent,
               disabledContainerColor = Color.Transparent,
               focusedIndicatorColor = Color.DarkGray,
               unfocusedIndicatorColor = Color.Gray,
               cursorColor = Color.Gray,
               focusedTextColor = Color.Black,
               unfocusedTextColor = Color.Black,
               focusedLabelColor = Color.Gray,
               unfocusedLabelColor = Color.Gray

               )
       )

        Spacer(Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = {password = it},
            singleLine = true,
            label = {Text("Senha")},
            leadingIcon = { Icon(painterResource(R.drawable.lock), null   ) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if(showPassword) VisualTransformation.None else
                PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(
                    onClick = { showPassword = !showPassword },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Gray // Ou qualquer outra cor que você queira
                    )
                ) {
                    Text(if (showPassword) "Hide" else "Show")
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.DarkGray,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = Color.Gray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Gray, // Cor do label quando o campo está focado
                unfocusedLabelColor = Color.Gray

            )
        )

        if(isSignUp){
            TextField(
                value = confirm,
                onValueChange = {confirm = it},
                singleLine = true,
                label = {Text("Confirme sua senha")},
                leadingIcon = { Icon(painterResource(R.drawable.lock), null   ) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.DarkGray,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Gray, // Cor do label quando o campo está focado
                    unfocusedLabelColor = Color.Gray

                )
            )
        }
        if(!isSignUp){
            Box(modifier= Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 8.dp)
            ){
                TextButton(onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryRed)
                    ) {
                    Text("Esqueceu sua Senha")
                }
            }

        }
        Spacer(Modifier.height(if (isSignUp)  32.dp else 24.dp))

        val isSignUp = mode == AuthMode.Signup
        Button(onClick = {
            if (isSignUp) {
                onPrimary(email, password, name)
            } else {
                onPrimary(email, password, "")
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed,
            contentColor = Color.White),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)) {
            Text("Entrar", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))


    }}











