package com.example.toindo.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toindo.R

private val PrimaryRed= Color(0xffe53935)
private val SoftRed=Color(0xffffe9e8)
private val SoftRedText=Color(0xffdb3a34)

enum class AuthMode{SignIn, Signup}

@Composable
fun SignInScreen(
    mode:AuthMode=AuthMode.SignIn,
    onPrimary:(String, String)->Unit={_,_->},
    onForgotPassword:()->Unit={},
    onForgotNumber:()->Unit={},
    onSwitch:()->Unit={},
){
    val isSignUp = mode == AuthMode.Signup

    var email by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}
    var confirm by remember {mutableStateOf("")}
    var showPassword by remember {mutableStateOf(false)}

    val titleText=if(isSignUp)"Sign Up" else "Sign In"
    val primaryText=if(isSignUp)"Create Account" else "Sign In"
    val bottomPrompt=if(isSignUp)"I already have an account" else "I am a new user?"
    val bottomLink = if (isSignUp) "Sign In" else "Sign Up"


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
                .height(370.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(text = titleText,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            textAlign = TextAlign.Start
            )
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
               cursorColor = MaterialTheme.colorScheme.primary

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
                TextButton(onClick = {showPassword = !showPassword}) {
                    Text(if(showPassword) "Hide" else "Show")
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
                cursorColor = MaterialTheme.colorScheme.primary

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
                    cursorColor = MaterialTheme.colorScheme.primary

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
            Button(
                onClick = onForgotPassword,
                colors = ButtonDefaults.buttonColors(containerColor = SoftRed,
                    contentColor = SoftRedText),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)

            ) {
                Text("Esqueceu seu número",
                    fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(if (isSignUp)  32.dp else 24.dp))

        Button(onClick = {onPrimary(email, password)},
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed,
                contentColor = Color.White),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
            ) {
            Text(primaryText, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))

        val annotated = buildAnnotatedString {
            append(bottomPrompt)
            pushStringAnnotation("switch", "switch")
            withStyle(SpanStyle(color = PrimaryRed, fontWeight = FontWeight.Bold))
            {
                append(bottomLink) }
            pop()
        }
        ClickableText(
            text = annotated,
            onClick = {off ->
        annotated.getStringAnnotations("switch", off, off).firstOrNull()?.let {
            onSwitch
        }},
            modifier = Modifier
                .padding(vertical = 18.dp)
                .navigationBarsPadding()
        )
    }}
    // Preview para o modo SignIn
    @Preview(showBackground = true, name = "SignIn Mode")
    @Composable
    fun SignInScreenPreview() {
        // Você pode envolver com um MaterialTheme se sua UI depende dele
        MaterialTheme {
            SignInScreen(mode = AuthMode.SignIn)
        }
    }

    // Preview para o modo SignUp
    @Preview(showBackground = true, name = "SignUp Mode")
    @Composable
    fun SignUpScreenPreview() {
        MaterialTheme {
            SignInScreen(mode = AuthMode.Signup)
        }
    }











