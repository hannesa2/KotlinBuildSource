package info.git.versionHelper

import info.git.versionHelper.ColoredConsole.Companion.BLACK
import info.git.versionHelper.ColoredConsole.Companion.BRIGHT_BLACK
import info.git.versionHelper.ColoredConsole.Companion.BRIGHT_WHITE
import info.git.versionHelper.ColoredConsole.Companion.RESET
import info.git.versionHelper.ColoredConsole.Companion.WHITE

// https://github.com/marcelmatula/colored-console
interface ColoredConsole {

    sealed class Style {

        @Suppress("unused")
        val bg: ColoredConsole.Style
            get() = when (this) {
                is Simple -> if (code.isColor) copy(code = code + BACKGROUND_SHIFT) else this
                is Composite -> if (parent is Simple && parent.code.isColor)
                    copy(parent = parent.copy(code = parent.code + BACKGROUND_SHIFT))
                else this

                is NotApplied -> this
            }

        val bright: ColoredConsole.Style
            get() = when (this) {
                is Simple -> if (code.isNormalColor) copy(code = code + BRIGHT_SHIFT) else this
                is Composite -> if (parent is Simple && parent.code.isNormalColor)
                    copy(parent = parent.copy(code = parent.code + BRIGHT_SHIFT))
                else this

                is NotApplied -> this
            }

        abstract fun wrap(text: String): String

        object NotApplied : ColoredConsole.Style() {
            override fun wrap(text: String) = text
        }

        data class Simple(val code: Int) : ColoredConsole.Style() {
            override fun wrap(text: String) = text.applyCodes(code)
        }

        data class Composite(val parent: ColoredConsole.Style, private val child: ColoredConsole.Style) : ColoredConsole.Style() {
            override fun wrap(text: String) = parent.wrap(child.wrap(text))
        }

        operator fun plus(style: ColoredConsole.Style) = when (this) {
            is NotApplied -> this
            is Simple -> Composite(style, this)
            is Composite -> Composite(style, this)
        }
    }

    fun <N> N.style(style: ColoredConsole.Style, predicate: (N) -> Boolean = { true }) =
        takeIf { predicate(this) }?.let { style.wrap(toString()) } ?: toString()

    operator fun <N> N.invoke(style: ColoredConsole.Style, predicate: (N) -> Boolean = { true }) = style(style, predicate)

    fun <N> N.wrap(vararg ansiCodes: Int) = toString().let { text ->
        if (this@ColoredConsole is ColorConsoleDisabled)
            text
        else {
            val codes = ansiCodes.filter { it != RESET }
            text.applyCodes(*codes.toIntArray())
        }
    }

    private val String.firstAnsi
        get() = reEscape.find(this)?.let { matcher ->
            if (matcher.range.start != 0) null else matcher.groups[1]?.value?.toIntOrNull()
        }

    val String.bright
        get() = firstAnsi.let { code ->
            if (code?.isNormalColor == true) substring(0, 2) + (code + BRIGHT_SHIFT) + substring(4) else this
        }

    val String.bg
        get() = firstAnsi.let { code ->
            if (code?.isColor == true) substring(0, 2) + (code + BACKGROUND_SHIFT) + substring(4) else this
        }

    // region styles
    val bold: ColoredConsole.Style get() = Style.Simple(HIGH_INTENSITY)
    val <N : ColoredConsole.Style> N.bold: ColoredConsole.Style get() = this + this@ColoredConsole.bold
    val <N> N.bold get() = wrap(HIGH_INTENSITY)
    fun <N> N.bold(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.bold ?: this.toString()
    fun bold(text: Any) = text.wrap(HIGH_INTENSITY)

    val faint: ColoredConsole.Style get() = Style.Simple(LOW_INTENSITY)
    val <N : ColoredConsole.Style> N.faint: ColoredConsole.Style get() = this + this@ColoredConsole.faint
    val <N> N.faint get() = wrap(LOW_INTENSITY)
    fun <N> N.faint(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.faint ?: this.toString()
    fun faint(text: Any) = text.wrap(LOW_INTENSITY)

    val italic: ColoredConsole.Style get() = Style.Simple(ITALIC)
    val <N : ColoredConsole.Style> N.italic: ColoredConsole.Style get() = this + this@ColoredConsole.italic
    val <N> N.italic get() = wrap(ITALIC)
    fun <N> N.italic(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.italic ?: this.toString()
    fun italic(text: String) = text.wrap(ITALIC)

    val underline: ColoredConsole.Style get() = Style.Simple(UNDERLINE)
    val <N : ColoredConsole.Style> N.underline: ColoredConsole.Style get() = this + this@ColoredConsole.underline
    val <N> N.underline get() = wrap(UNDERLINE)
    fun <N> N.underline(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.underline ?: this.toString()
    fun underline(text: String) = text.wrap(UNDERLINE)

    val blink: ColoredConsole.Style get() = Style.Simple(BLINK)
    val <N : ColoredConsole.Style> N.blink: ColoredConsole.Style get() = this + this@ColoredConsole.blink
    val <N> N.blink get() = wrap(BLINK)
    fun <N> N.blink(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.blink ?: this.toString()
    fun blink(text: String) = text.wrap(BLINK)

    val reverse: ColoredConsole.Style get() = Style.Simple(REVERSE)
    val <N : ColoredConsole.Style> N.reverse: ColoredConsole.Style get() = this + this@ColoredConsole.reverse
    val <N> N.reverse get() = wrap(REVERSE)
    fun <N> N.reverse(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.reverse ?: this.toString()
    fun reverse(text: String) = text.wrap(REVERSE)

    val hidden: ColoredConsole.Style get() = Style.Simple(HIDDEN)
    val <N : ColoredConsole.Style> N.hidden: ColoredConsole.Style get() = this + this@ColoredConsole.hidden
    val <N> N.hidden get() = wrap(HIDDEN)
    fun <N> N.hidden(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.hidden ?: this.toString()
    fun hidden(text: String) = text.wrap(HIDDEN)

    val strike: ColoredConsole.Style get() = Style.Simple(STRIKE)
    val <N : ColoredConsole.Style> N.strike: ColoredConsole.Style get() = this + this@ColoredConsole.strike
    val <N> N.strike get() = wrap(STRIKE)
    fun <N> N.strike(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.strike ?: this.toString()
    fun strike(text: String) = text.wrap(STRIKE)
    // endregion

    // region colors
    val black: ColoredConsole.Style get() = Style.Simple(BLACK)
    val <N : ColoredConsole.Style> N.black: ColoredConsole.Style get() = this + this@ColoredConsole.black
    val <N> N.black get() = wrap(BLACK)
    fun <N> N.black(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.black ?: toString()
    fun black(text: String) = text.wrap(BLACK)

    val red: ColoredConsole.Style get() = Style.Simple(RED)
    val <N : ColoredConsole.Style> N.red: ColoredConsole.Style get() = this + this@ColoredConsole.red
    val <N> N.red get() = wrap(RED)
    fun <N> N.red(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.red ?: toString()
    fun red(text: String) = text.wrap(RED)

    val green: ColoredConsole.Style get() = Style.Simple(GREEN)
    val <N : ColoredConsole.Style> N.green: ColoredConsole.Style get() = this + this@ColoredConsole.green
    val <N> N.green get() = wrap(GREEN)
    fun <N> N.green(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.green ?: toString()
    fun green(text: String) = text.wrap(GREEN)

    val yellow: ColoredConsole.Style get() = Style.Simple(YELLOW)
    val <N : ColoredConsole.Style> N.yellow: ColoredConsole.Style get() = this + this@ColoredConsole.yellow
    val <N> N.yellow get() = wrap(YELLOW)
    fun <N> N.yellow(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.yellow ?: toString()
    fun yellow(text: String) = text.wrap(YELLOW)

    val blue: ColoredConsole.Style get() = Style.Simple(BLUE)
    val <N : ColoredConsole.Style> N.blue: ColoredConsole.Style get() = this + this@ColoredConsole.blue
    val <N> N.blue get() = wrap(BLUE)
    fun <N> N.blue(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.blue ?: toString()
    fun blue(text: String) = text.wrap(BLUE)

    val purple: ColoredConsole.Style get() = Style.Simple(PURPLE)
    val <N : ColoredConsole.Style> N.purple: ColoredConsole.Style get() = this + this@ColoredConsole.purple
    val <N> N.purple get() = wrap(PURPLE)
    fun <N> N.purple(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.purple ?: toString()
    fun purple(text: String) = text.wrap(PURPLE)

    val cyan: ColoredConsole.Style get() = Style.Simple(CYAN)
    val <N : ColoredConsole.Style> N.cyan: ColoredConsole.Style get() = this + this@ColoredConsole.cyan
    val <N> N.cyan get() = wrap(CYAN)
    fun <N> N.cyan(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.cyan ?: toString()
    fun cyan(text: String) = text.wrap(CYAN)

    val white: ColoredConsole.Style get() = Style.Simple(WHITE)
    val <N : ColoredConsole.Style> N.white: ColoredConsole.Style get() = this + this@ColoredConsole.white
    val <N> N.white get() = wrap(WHITE)
    fun <N> N.white(predicate: (N) -> Boolean = { true }) = takeIf { predicate(this) }?.toString()?.white ?: toString()
    fun white(text: String) = text.wrap(WHITE)
    // endregion

    companion object {
        const val RESET = 0

        const val HIGH_INTENSITY = 1
        const val LOW_INTENSITY = 2

        const val BACKGROUND_SHIFT = 10
        const val BRIGHT_SHIFT = 60

        const val ITALIC = 3
        const val UNDERLINE = 4
        const val BLINK = 5
        const val REVERSE = 7
        const val HIDDEN = 8
        const val STRIKE = 9

        const val BLACK = 30
        const val RED = 31
        const val GREEN = 32
        const val YELLOW = 33
        const val BLUE = 34
        const val PURPLE = 35
        const val CYAN = 36
        const val WHITE = 37

        const val BRIGHT_BLACK = BLACK + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_RED = RED + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_GREEN = GREEN + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_YELLOW = YELLOW + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_BLUE = BLUE + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_PURPLE = PURPLE + BRIGHT_SHIFT

        @Suppress("unused")
        const val BRIGHT_CYAN = CYAN + BRIGHT_SHIFT

        const val BRIGHT_WHITE = WHITE + BRIGHT_SHIFT

        val reEscape = Regex("\\u001B\\[([0-9]{1,2})m")
    }
}

private interface ColorConsoleDisabled : ColoredConsole {

    override val bold get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.bold: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val italic get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.italic: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val underline get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.underline: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val blink get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.blink: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val reverse get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.reverse: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val hidden get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.hidden: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied

    override val red get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.red: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val black get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.black: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val green get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.green: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val yellow get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.yellow: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val blue get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.blue: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val purple get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.purple: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val cyan get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.cyan: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
    override val white get() = ColoredConsole.Style.NotApplied
    override val <N : ColoredConsole.Style> N.white: ColoredConsole.Style get() = ColoredConsole.Style.NotApplied
}

private val Int.isNormalColor get() = this in BLACK..WHITE
private val Int.isBrightColor get() = this in BRIGHT_BLACK..BRIGHT_WHITE
private val Int.isColor get() = isNormalColor || isBrightColor

private fun String.applyCodes(vararg codes: Int) = "\u001B[${RESET}m".let { reset ->
    val tags = codes.joinToString { "\u001B[${it}m" }
    split(reset).filter { it.isNotEmpty() }.joinToString(separator = "") { tags + it + reset }
}

//@UseExperimental(ExperimentalContracts::class)
fun <R> colored(enabled: Boolean = true, block: ColoredConsole.() -> R): R {
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    check(true)
    return if (enabled) object : ColoredConsole {}.block() else object : ColorConsoleDisabled {}.block()
}

fun <R : ColoredConsole.Style> style(block: ColoredConsole.() -> R): R = object : ColoredConsole {}.block()

@Suppress("unused")
fun print(colored: Boolean = true, block: ColoredConsole.() -> String) = colored(colored) { print(block()) }

fun println(colored: Boolean = true, block: ColoredConsole.() -> String) = colored(colored) { println(block()) }