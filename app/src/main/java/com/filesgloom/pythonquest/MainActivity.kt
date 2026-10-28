package com.filesgloom.pythonquest

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.chaquo.python.Python

private data class Lesson(
    val title: String, val subtitle: String, val explanation: String,
    val example: String, val starter: String, val hint: String,
    val expected: (String) -> Boolean
)

class MainActivity : Activity() {
    private val bg = Color.rgb(11, 16, 13)
    private val panel = Color.rgb(20, 29, 23)
    private val panelAlt = Color.rgb(27, 43, 32)
    private val green = Color.rgb(140, 240, 167)
    private val white = Color.rgb(240, 246, 240)
    private val muted = Color.rgb(163, 180, 167)
    private var currentLesson = 0
    private var xp = 0
    private val completed = mutableSetOf<Int>()
    private lateinit var root: LinearLayout
    private lateinit var editor: EditText
    private lateinit var output: TextView
    private lateinit var lessonTitle: TextView
    private lateinit var lessonText: TextView
    private lateinit var example: TextView
    private lateinit var stats: TextView
    private lateinit var progressText: TextView
    private lateinit var courseProgress: ProgressBar
    private lateinit var levelProgress: ProgressBar
    private lateinit var levelProgressText: TextView
    private lateinit var lessonButtons: LinearLayout

    private val lessons = listOf(
        Lesson("Первая программа", "Выведи текст на экран",
            "Функция print() выводит информацию на экран. Текст заключай в кавычки.",
            "print(\"Привет, мир!\")", "print(\"Привет, мир!\")",
            "Напиши print(\"Привет, мир!\") — сохрани скобки и кавычки.",
            { it.trim() == "Привет, мир!" }),
        Lesson("Переменные", "Сохраняй данные",
            "Переменная хранит значение. Текст записывают в кавычках, число — без кавычек.",
            "city = \"Теплодар\"\nyear = 2026\nprint(city)\nprint(year)",
            "city = \"Теплодар\"\nyear = 2026\nprint(city)\nprint(year)",
            "Создай переменные city и year, затем выведи обе через print().",
            { it.contains("Теплодар") && it.contains("2026") }),
        Lesson("Математика", "Вычисляй с Python",
            "Python умеет складывать, вычитать, умножать и делить. Выражение можно передать в print().",
            "print(7 + 3)", "print(7 + 3)", "Используй print(7 + 3). Результат должен быть 10.",
            { it.trim() == "10" }),
        Lesson("Условия if", "Принимай решения",
            "if выполняет код, когда условие истинно. После условия поставь двоеточие, тело сдвинь на четыре пробела.",
            "age = 18\nif age >= 18:\n    print(\"Можно\")",
            "age = 18\nif age >= 18:\n    print(\"Можно\")",
            "Проверь age >= 18 и выведи слово Можно внутри блока if.",
            { it.trim() == "Можно" }),
        Lesson("Цикл for", "Повторяй действия",
            "Цикл for повторяет действие для каждого значения. range(3) выдаёт числа 0, 1 и 2.",
            "for i in range(3):\n    print(i)", "for i in range(3):\n    print(i)",
            "Напиши for i in range(3): и внутри цикла print(i).",
            { it.trim() == "0\n1\n2" }),
        Lesson("Строки", "Объединяй текст",
            "Строки — это текст в кавычках. Их можно соединять оператором +.",
            "name = \"Мир\"\nprint(\"Привет, \" + name + \"!\")",
            "name = \"Мир\"\nprint(\"Привет, \" + name + \"!\")",
            "Создай переменную name и выведи приветствие через оператор +.",
            { it.trim() == "Привет, Мир!" }),
        Lesson("Списки", "Храни наборы значений",
            "Список хранит несколько значений. Индексация начинается с нуля, а len() возвращает длину списка.",
            "fruits = [\"яблоко\", \"груша\"]\nprint(fruits[0])\nprint(len(fruits))",
            "fruits = [\"яблоко\", \"груша\"]\nprint(fruits[0])\nprint(len(fruits))",
            "Выведи первый элемент списка и количество элементов.",
            { it.trim() == "яблоко\n2" }),
        Lesson("Цикл while", "Повторяй пока условие верно",
            "while повторяет блок, пока условие истинно. Не забывай менять счётчик, иначе цикл может стать бесконечным.",
            "counter = 1\nwhile counter <= 3:\n    print(counter)\n    counter += 1",
            "counter = 1\nwhile counter <= 3:\n    print(counter)\n    counter += 1",
            "Выведи числа от 1 до 3 с помощью while и увеличивай counter на 1.",
            { it.trim() == "1\n2\n3" }),
        Lesson("Функции", "Создавай свои команды",
            "Функция начинается с def. Она может принимать параметры и возвращать результат через return.",
            "def square(number):\n    return number * number\n\nprint(square(5))",
            "def square(number):\n    return number * number\n\nprint(square(5))",
            "Создай функцию square(number), которая возвращает число, умноженное на себя.",
            { it.trim() == "25" }),
        Lesson("Словари", "Храни данные по ключам",
            "Словарь хранит пары ключ-значение. Получай значение по ключу, например player[\"name\"].",
            "player = {\"name\": \"Алекс\", \"level\": 3}\nprint(player[\"name\"])\nprint(player[\"level\"])",
            "player = {\"name\": \"Алекс\", \"level\": 3}\nprint(player[\"name\"])\nprint(player[\"level\"])",
            "Создай словарь player с ключами name и level и выведи оба значения.",
            { it.trim() == "Алекс\n3" }),
        Lesson("Логические операторы", "Объединяй условия",
            "Оператор and требует, чтобы оба условия были истинны. Оператор or принимает хотя бы одно истинное условие, а not меняет True на False и наоборот.",
            "age = 20\nhas_ticket = True\nprint(age >= 18 and has_ticket)",
            "age = 20\nhas_ticket = True\nprint(age >= 18 and has_ticket)",
            "Создай age = 20 и has_ticket = True. Выведи age >= 18 and has_ticket.",
            { it.trim() == "True" }),
        Lesson("Операторы сравнения", "Сравнивай значения",
            "Операторы сравнения возвращают True или False. Например, == проверяет равенство, != — неравенство, а >= означает «больше или равно».",
            "score = 75\nprint(score >= 60)\nprint(score == 100)",
            "score = 75\nprint(score >= 60)\nprint(score == 100)",
            "Создай score = 75. Выведи результат сравнения score >= 60, затем score == 100.",
            { it.trim() == "True\nFalse" }),
        Lesson("Методы строк", "Изменяй текст командами",
            "У строк есть полезные методы. upper() переводит буквы в верхний регистр, lower() — в нижний, а capitalize() делает первую букву заглавной.",
            "word = \"python\"\nprint(word.upper())\nprint(word.capitalize())",
            "word = \"python\"\nprint(word.upper())\nprint(word.capitalize())",
            "Создай word = \"python\". Выведи word.upper(), а затем word.capitalize().",
            { it.trim() == "PYTHON\nPython" }),
        Lesson("F-строки", "Вставляй значения в текст",
            "F-строки позволяют подставлять значения переменных внутрь текста. Поставь букву f перед кавычками, а имя переменной заключи в фигурные скобки.",
            "name = \"Мир\"\nprint(f\"Привет, {name}!\")",
            "name = \"Мир\"\nprint(f\"Привет, {name}!\")",
            "Создай name = \"Мир\" и выведи приветствие с помощью f\"Привет, {name}!\".",
            { it.trim() == "Привет, Мир!" }),
        Lesson("Методы списков", "Добавляй и удаляй элементы",
            "Метод append() добавляет элемент в конец списка. Метод remove() удаляет первое совпадающее значение, а len() показывает количество элементов.",
            "tasks = [\"учиться\", \"практиковаться\"]\ntasks.append(\"повторять\")\nprint(len(tasks))\ntasks.remove(\"учиться\")\nprint(tasks)",
            "tasks = [\"учиться\", \"практиковаться\"]\ntasks.append(\"повторять\")\nprint(len(tasks))\ntasks.remove(\"учиться\")\nprint(tasks)",
            "Создай список tasks, добавь элемент через append(), выведи длину, удали первый элемент через remove() и выведи список.",
            { it.trim() == "3\n['практиковаться', 'повторять']" })
        ,Lesson("Обработка ошибок", "Управляй ошибками через try/except",
            "Блок try содержит код, который может завершиться ошибкой. except перехватывает указанный тип ошибки и позволяет программе продолжить работу. Перехватывай конкретные исключения, например ValueError.",
            "try:\n    number = int(\"abc\")\nexcept ValueError:\n    print(\"Это не число\")",
            "try:\n    number = int(\"abc\")\nexcept ValueError:\n    print(\"Это не число\")",
            "Внутри try попробуй преобразовать строку \"abc\" в int, а в except ValueError выведи Это не число.",
            { it.trim() == "Это не число" }),
        Lesson("Модули и import", "Используй готовые инструменты Python",
            "Модуль — это файл с готовыми функциями и данными. Команда import подключает модуль, после чего можно обращаться к его функциям через точку. Например, math.sqrt() вычисляет квадратный корень.",
            "import math\nprint(math.sqrt(16))",
            "import math\nprint(math.sqrt(16))",
            "Подключи модуль math и выведи квадратный корень из 16 с помощью math.sqrt(16).",
            { it.trim() == "4.0" }),
        Lesson("Множества и кортежи", "Храни уникальные и неизменяемые данные",
            "Множество set хранит только уникальные элементы, поэтому повторения удаляются. Кортеж tuple похож на список, но его элементы нельзя изменить после создания.",
            "numbers = {1, 2, 2, 3}\nprint(len(numbers))\npoint = (4, 5)\nprint(point[0])",
            "numbers = {1, 2, 2, 3}\nprint(len(numbers))\npoint = (4, 5)\nprint(point[0])",
            "Создай множество {1, 2, 2, 3} и выведи его длину. Затем создай кортеж (4, 5) и выведи первый элемент.",
            { it.trim() == "3\n4" }),
        Lesson("Работа с файлами", "Читай и записывай текстовые данные",
            "Функция open() открывает файл. Режим w записывает текст и создаёт файл, если его нет; режим r читает существующий файл. Конструкция with автоматически закрывает файл после работы.",
            "with open(\"notes.txt\", \"w\", encoding=\"utf-8\") as file:\n    file.write(\"Привет!\")\nwith open(\"notes.txt\", \"r\", encoding=\"utf-8\") as file:\n    print(file.read())",
            "with open(\"notes.txt\", \"w\", encoding=\"utf-8\") as file:\n    file.write(\"Привет!\")\nwith open(\"notes.txt\", \"r\", encoding=\"utf-8\") as file:\n    print(file.read())",
            "Используй with open(\"notes.txt\", \"w\", encoding=\"utf-8\") для записи текста Привет!, затем открой файл в режиме r и выведи file.read().",
            { it.trim() == "Привет!" })
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val prefs = getSharedPreferences("python_quest", MODE_PRIVATE)
        xp = prefs.getInt("xp", 0)
        for (i in lessons.indices) if (prefs.getBoolean("done_$i", false)) completed.add(i)
        buildUi()
        showLesson(0)
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg); clipToPadding = false }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(30))
        }
        scroll.addView(root)
        setContentView(scroll)

        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val logo = text("Py", 22f, Color.rgb(8, 33, 15), true).apply {
            gravity = Gravity.CENTER
            background = rounded(green, 14)
        }
        header.addView(logo, LinearLayout.LayoutParams(dp(48), dp(48)))
        header.addView(text("  PYTHON QUEST", 19f, white, true),
            LinearLayout.LayoutParams(0, dp(48), 1f).apply { gravity = Gravity.CENTER_VERTICAL })
        root.addView(header)

        val hero = card().apply { setPadding(dp(20), dp(20), dp(20), dp(20)) }
        addText(hero, "ТВОЙ ПУТЬ В ПРОГРАММИРОВАНИЕ", 11f, green, true)
        addText(hero, "Изучай Python.\nПрокачивай навыки.", 29f, white, true)
        addText(hero, "Короткие уроки на русском, практика и уровни — прямо на телефоне.", 15f, muted, false)
        stats = addText(hero, "", 14f, green, true)
        courseProgress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = lessons.size
            progress = completed.size
            progressTintList = ColorStateList.valueOf(green)
            progressBackgroundTintList = ColorStateList.valueOf(panelAlt)
            minimumHeight = dp(8)
        }
        hero.addView(courseProgress, params(top = 5, bottom = 2))
        progressText = addText(hero, "", 12f, muted, false)
        addText(hero, "ПРОГРЕСС УРОВНЯ", 10f, green, true)
        levelProgress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = xp % 100
            progressTintList = ColorStateList.valueOf(green)
            progressBackgroundTintList = ColorStateList.valueOf(panelAlt)
            minimumHeight = dp(8)
        }
        hero.addView(levelProgress, params(top = 2, bottom = 2))
        levelProgressText = addText(hero, "", 12f, muted, false)
        root.addView(hero, params(top = 18, bottom = 16))

        addText(root, "ТВОИ УРОКИ", 13f, green, true)
        lessonButtons = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(lessonButtons, params(top = 8, bottom = 18))
        lessons.forEachIndexed { index, lesson ->
            val button = Button(this).apply {
                text = "${index + 1}.  ${lesson.title}\n      ${lesson.subtitle}"
                textSize = 14f
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                setTextColor(white)
                isAllCaps = false
                background = rounded(panel, 14)
                setPadding(dp(14), dp(5), dp(10), dp(5))
                minHeight = dp(62)
                setOnClickListener { showLesson(index) }
            }
            lessonButtons.addView(button, params(bottom = 7))
        }

        val lessonCard = card().apply { setPadding(dp(18), dp(18), dp(18), dp(18)) }
        lessonTitle = addText(lessonCard, "", 23f, white, true)
        lessonText = addText(lessonCard, "", 15f, muted, false)
        addText(lessonCard, "ПРИМЕР", 11f, green, true)
        example = addText(lessonCard, "", 14f, green, false).apply {
            typeface = Typeface.MONOSPACE
            background = rounded(Color.rgb(7, 12, 8), 10)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        addText(lessonCard, "ТВОЙ КОД PYTHON", 11f, green, true)
        editor = EditText(this).apply {
            setTextColor(white)
            setHintTextColor(muted)
            textSize = 14f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.TOP or Gravity.START
            setPadding(dp(12), dp(12), dp(12), dp(12))
            minLines = 5
            background = rounded(Color.rgb(7, 12, 8), 10)
            hint = "Напиши код здесь…"
            setSingleLine(false)
        }
        lessonCard.addView(editor, params(top = 7, bottom = 12))
        val runButton = Button(this).apply {
            text = "▶  Запустить код"
            isAllCaps = false
            setTextColor(Color.rgb(8, 33, 15))
            background = rounded(green, 12)
            setOnClickListener { runCode(false) }
        }
        lessonCard.addView(runButton, params(bottom = 7))
        val hintButton = Button(this).apply {
            text = "Подсказка"
            isAllCaps = false
            setTextColor(white)
            background = rounded(panelAlt, 12)
            setOnClickListener { showOutput("Подсказка: ${lessons[currentLesson].hint}", false) }
        }
        lessonCard.addView(hintButton, params(bottom = 7))
        output = addText(lessonCard, "Результат появится здесь.", 13f, muted, false).apply {
            typeface = Typeface.MONOSPACE
            background = rounded(Color.rgb(7, 12, 8), 10)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        val checkButton = Button(this).apply {
            text = "✓  Проверить задание"
            isAllCaps = false
            setTextColor(white)
            background = rounded(panelAlt, 12)
            setOnClickListener { runCode(true) }
        }
        lessonCard.addView(checkButton, params(top = 10))
        root.addView(lessonCard)
        addText(root, "Прогресс сохраняется на этом устройстве.", 12f, muted, false)
    }

    private fun showLesson(index: Int) {
        currentLesson = index
        val lesson = lessons[index]
        lessonTitle.text = "Урок ${index + 1}. ${lesson.title}"
        lessonText.text = lesson.explanation
        example.text = lesson.example
        editor.setText(lesson.starter)
        showOutput("Запусти пример или напиши собственный код.", false)
        for (i in 0 until lessonButtons.childCount) {
            val button = lessonButtons.getChildAt(i) as Button
            button.background = rounded(if (i == index) panelAlt else panel, 14)
            button.text = "${if (completed.contains(i)) "✓" else (i + 1).toString()}.  ${lessons[i].title}\n      ${lessons[i].subtitle}"
        }
        val level = 1 + xp / 100
        val xpIntoLevel = xp % 100
        stats.text = "$xp XP   •   Уровень $level   •   Уроков пройдено: ${completed.size}/${lessons.size}"
        courseProgress.progress = completed.size
        progressText.text = "Курс пройден на ${completed.size * 100 / lessons.size}%"
        levelProgress.progress = xpIntoLevel
        levelProgressText.text = "$xpIntoLevel / 100 XP до уровня ${level + 1}"
    }

    private fun runCode(check: Boolean) {
        try {
            val runner = Python.getInstance().getModule("runner")
            val result = runner.callAttr("run_code", editor.text.toString())
                ?: throw IllegalStateException("Python runner вернул пустой результат")
            val ok = result.get("ok")?.toBoolean() ?: false
            val resultText = result.get("output")?.toString() ?: ""
            if (!ok) {
                showOutput(resultText, false)
                return
            }
            if (check) {
                if (lessons[currentLesson].expected(resultText)) {
                    val firstCompletion = completed.add(currentLesson)
                    if (firstCompletion) {
                        xp += 20
                        getSharedPreferences("python_quest", MODE_PRIVATE).edit()
                            .putInt("xp", xp)
                            .putBoolean("done_$currentLesson", true)
                            .apply()
                    }
                    showLesson(currentLesson)
                    showOutput("Верно! ${if (firstCompletion) "+20 XP получено." else "Урок уже пройден."}\n\n$resultText", true)
                } else {
                    showOutput("Пока не совпало с ожидаемым результатом. Проверь код и попробуй ещё раз.\n\nВывод:\n$resultText", false)
                }
            } else {
                showOutput(resultText, true)
            }
        } catch (e: Exception) {
            showOutput("Не удалось запустить Python: ${e.message ?: e.javaClass.simpleName}", false)
        }
    }

    private fun showOutput(message: String, success: Boolean) {
        output.text = message
        output.setTextColor(if (success) green else muted)
    }

    private fun card() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = rounded(panel, 18)
    }

    private fun addText(parent: LinearLayout, value: String, size: Float, color: Int, bold: Boolean): TextView {
        val view = text(value, size, color, bold)
        parent.addView(view, params(top = 4, bottom = 8))
        return view
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setLineSpacing(dp(2).toFloat(), 1f)
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
        if (color == panel || color == panelAlt) setStroke(dp(1), Color.rgb(43, 59, 48))
    }

    private fun params(top: Int = 0, bottom: Int = 0) =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
