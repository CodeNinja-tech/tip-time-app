package dev.codeninja.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.codeninja.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			TipTimeTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
					TipTimeScreen()
				}
			}
		}
	}
}

@Composable
fun TipTimeScreen() {
	var amountInput by remember { mutableStateOf("") }
	var tipPercentInput by remember { mutableStateOf("") }
	var roundUp by remember { mutableStateOf(false) }

	val amount = amountInput.toDoubleOrNull() ?: 0.0
	val tipPercent = tipPercentInput.toDoubleOrNull() ?: 0.0
	val tip = calculateTip(amount, tipPercent, roundUp)

	val focusManager = LocalFocusManager.current

	Column(
		modifier = Modifier.padding(32.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = stringResource(R.string.calculate_tip),
			fontSize = 24.sp,
			modifier = Modifier.align(Alignment.CenterHorizontally),
			style = TextStyle(
				color = colorResource(id = R.color.purple_200)
			)
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Bill amount text field
		EditNumberField(
			value = amountInput,
			onValueChange = { amountInput = it},
			label = R.string.bill_amount,
			keyboardOptions = KeyboardOptions.Default.copy(
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Next
			),
			keyboardActions = KeyboardActions(
				onNext = {
					focusManager.moveFocus(focusDirection = FocusDirection.Down)
				}
			)
		)

		// Tip Percentage text field
		EditNumberField(
			value = tipPercentInput,
			onValueChange = { tipPercentInput = it },
			label = R.string.how_was_the_service,
			keyboardOptions = KeyboardOptions.Default.copy(
				keyboardType = KeyboardType.Number,
				imeAction = ImeAction.Go
			),
			keyboardActions = KeyboardActions(
				onGo = {
					focusManager.clearFocus()
				}
			)
		)

		RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = { roundUp = it })

		Spacer(modifier = Modifier.height(24.dp))

		Text(
			text = stringResource(id = R.string.tip_amount, tip),
			modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
			fontSize = 20.sp,
			fontWeight = FontWeight.Bold
		)
	}
}

@Composable
fun EditNumberField(
	value: String,
	onValueChange: (String) -> Unit,
	@StringRes label: Int,
	keyboardOptions: KeyboardOptions,
	keyboardActions: KeyboardActions
) {
	TextField(
		value = value,
		onValueChange = onValueChange,
		label = { Text(text = stringResource(id = label)) },
		modifier = Modifier.fillMaxWidth(),
		singleLine = true,
		keyboardOptions = keyboardOptions,
		keyboardActions = keyboardActions
	)
}

@Composable
fun RoundTheTipRow(
	modifier: Modifier = Modifier,
	roundUp: Boolean,
	onRoundUpChanged: (Boolean) -> Unit
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.size(48.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = stringResource(id = R.string.round_up_tip),
			style = TextStyle(
				color = colorResource(id = R.color.purple_200)
			)
		)
		Switch(
			checked = roundUp,
			onCheckedChange = onRoundUpChanged,
			modifier = modifier
				.fillMaxWidth()
				.wrapContentWidth(align = Alignment.End),
			colors = SwitchDefaults.colors(
				uncheckedThumbColor = Color.Gray,
				checkedThumbColor = colorResource(id = R.color.purple_200)
			)
		)
	}
}

private fun calculateTip(
	amount: Double,
	tipPercent: Double = 15.0,
	roundUp: Boolean
): String {
	var tip = (tipPercent / 100) * amount
	if (roundUp)
		tip = ceil(tip)
	return NumberFormat.getNumberInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	TipTimeTheme {
		TipTimeScreen()
	}
}