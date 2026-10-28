package com.filesgloom.pythonquest

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

private data class QuizQuestion(
    val section: Int,
    val prompt: String,
    val code: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String,
    val hint: String
)

class QuizActivity : Activity() {
    private val bg = Color.rgb(11, 16, 13)
    private val panel = Color.rgb(20, 29, 23)
    private val panelAlt = Color.rgb(27, 43, 32)
    private val green = Color.rgb(140, 240, 167)
    private val white = Color.rgb(240, 246, 240)
    private val muted = Color.rgb(163, 180, 167)

    private val sectionNames = listOf(
        "Первые шаги", "Данные и циклы", "Уверенный Python",
        "Инструменты работы с данными", "Мини-проекты",
        "Классы и гибкие функции", "Анализ и структурирование",
        "Файлы, форматы и алгоритмы",
        "Коллекции и итераторы", "Продвинутые функции", "Проектирование на ООП",
        "Ошибки и тестирование", "Файлы и форматы", "Базы данных и API",
        "Алгоритмы и сложность", "Практика разработчика"
    )

    private val questions = listOf(
        QuizQuestion(0, "Что выведет эта программа?", "name = \"Мир\"\nprint(\"Привет, \" + name)", listOf("Привет, name", "Привет, Мир", "name Мир", "Будет ошибка"), 1, "Переменная name хранит текст «Мир». Оператор + соединяет две строки, поэтому выводится «Привет, Мир».", "Посмотри, что хранится в name, и вспомни, что делает оператор + со строками."),
        QuizQuestion(0, "Какой тип у значения 12?", "value = 12", listOf("str — текст", "bool — логическое значение", "int — целое число", "list — список"), 2, "Число 12 записано без кавычек, поэтому это целое число типа int.", "Текст записывают в кавычках, а целые числа — без них."),
        QuizQuestion(0, "Когда выполнится команда внутри if?", "temperature = 5\nif temperature > 0:\n    print(\"Тепло\")", listOf("Всегда", "Когда temperature больше нуля", "Когда temperature равна нулю", "Никогда"), 1, "Условие temperature > 0 истинно для 5, поэтому команда print() выполнится.", "Знак > означает «строго больше». Сравни 5 и 0."),
        QuizQuestion(1, "Что выведет цикл?", "for i in range(3):\n    print(i)", listOf("1, 2, 3", "0, 1, 2", "0, 1, 2, 3", "Только 3"), 1, "range(3) создаёт последовательность 0, 1, 2. Число 3 в неё не входит.", "Вспомни: range начинает с нуля, а верхняя граница не включается."),
        QuizQuestion(1, "Как получить первый элемент списка?", "colors = [\"красный\", \"синий\"]", listOf("colors[1]", "colors.first()", "colors[0]", "colors[-0] не существует"), 2, "Индексация списка начинается с нуля, поэтому colors[0] — первый элемент.", "Первый индекс в Python равен 0."),
        QuizQuestion(1, "Что делает len(values)?", "values = [4, 8, 15]", listOf("Складывает числа", "Возвращает количество элементов", "Сортирует список", "Удаляет последний элемент"), 1, "len() возвращает длину последовательности. В списке values три элемента.", "len — сокращение от length, то есть «длина»."),
        QuizQuestion(2, "Какой результат сравнения 7 == 7?", "print(7 == 7)", listOf("7", "False", "True", "Будет SyntaxError"), 2, "Оператор == сравнивает значения. Числа равны, поэтому результат — True.", "Не путай == (сравнение) и = (присваивание)."),
        QuizQuestion(2, "Что выведет код?", "word = \"Python\"\nprint(word.upper())", listOf("python", "PYTHON", "Python.upper", "Ошибка"), 1, "Метод upper() возвращает строку, в которой все буквы преобразованы в верхний регистр.", "Вспомни английское слово upper — «верхний»."),
        QuizQuestion(2, "Какой тип ошибки возникает при обращении к неизвестной переменной?", "print(score)", listOf("NameError", "IndexError", "ZeroDivisionError", "KeyError"), 0, "NameError означает, что Python не знает имя. Проверь, объявлена ли переменная до использования.", "Переменная score нигде не создана до вызова print()."),
        QuizQuestion(3, "Для чего обычно используют import math?", "import math\nprint(math.sqrt(9))", listOf("Для создания списка", "Для подключения математических функций", "Для чтения JSON автоматически", "Для объявления класса"), 1, "Модуль math содержит математические функции, в том числе sqrt() для вычисления квадратного корня.", "Модуль — это набор готовых инструментов. Название math связано с математикой."),
        QuizQuestion(3, "Что вернёт sorted([3, 1, 2])?", "numbers = [3, 1, 2]", listOf("[3, 1, 2]", "[1, 2, 3]", "6", "None"), 1, "sorted() возвращает новый список, отсортированный по возрастанию: [1, 2, 3].", "sorted — «отсортированный». Исходный список при этом не требуется менять."),
        QuizQuestion(3, "Что хранит словарь?", "user = {\"name\": \"Аня\", \"age\": 20}", listOf("Только числа", "Пары ключ — значение", "Только уникальные значения без ключей", "Только строки в порядке сортировки"), 1, "Словарь хранит пары ключ — значение. Например, ключ name связан со значением «Аня».", "В примере name и age — ключи, а «Аня» и 20 — соответствующие значения."),
        QuizQuestion(4, "Что стоит проверить при пустом списке перед обращением items[0]?", "items = []", listOf("Что список не пуст", "Что все элементы строки", "Что список отсортирован", "Ничего проверять не нужно"), 0, "У пустого списка нет элемента с индексом 0. Проверка длины или пустоты предотвращает IndexError.", "Попробуй представить, существует ли первый элемент, если элементов ноль."),
        QuizQuestion(4, "Какое условие проверяет, что число n чётное?", "n = 8", listOf("n / 2 == 0", "n % 2 == 0", "n // 2 == 0", "n * 2 == 0"), 1, "Остаток от деления чётного числа на 2 равен нулю. Оператор % возвращает остаток.", "Нужен оператор, который вычисляет остаток от деления."),
        QuizQuestion(4, "Зачем разбивать программу на функции?", "", listOf("Чтобы код нельзя было повторно использовать", "Чтобы дать логическим частям имена и переиспользовать их", "Чтобы Python не выполнял код", "Только ради увеличения числа строк"), 1, "Функции помогают разделить задачу на понятные части, повторно использовать код и упростить проверку.", "Подумай, как не копировать один и тот же набор команд много раз."),
        QuizQuestion(5, "Что делает __init__ в обычном классе Python?", "class Cat:\n    def __init__(self, name):\n        self.name = name", listOf("Удаляет объект", "Инициализирует новый объект при создании", "Запускает бесконечный цикл", "Сортирует атрибуты"), 1, "Метод __init__ настраивает атрибуты объекта при создании экземпляра класса.", "Он вызывается при создании экземпляра, чтобы задать начальные данные."),
        QuizQuestion(5, "Что вернёт функция?", "def double(x):\n    return x * 2\n\nprint(double(4))", listOf("4", "6", "8", "x * 2"), 2, "При вызове double(4) параметр x равен 4, а return возвращает 4 * 2, то есть 8.", "Подставь 4 вместо x и вычисли произведение."),
        QuizQuestion(5, "Для чего нужен базовый случай в рекурсии?", "", listOf("Чтобы функция вызывала себя бесконечно", "Чтобы остановить дальнейшие рекурсивные вызовы", "Чтобы автоматически создать список", "Чтобы преобразовать число в текст"), 1, "Базовый случай завершает рекурсию. Без него функция может вызывать себя до ошибки RecursionError.", "Нужна ситуация, в которой функция больше не вызывает саму себя."),
        QuizQuestion(6, "Что вернёт any([False, True, False])?", "", listOf("False", "True", "3", "None"), 1, "any() возвращает True, если хотя бы один элемент последовательности истинный.", "any означает «хотя бы один». В списке есть True."),
        QuizQuestion(6, "Как получить наибольшее число из списка?", "scores = [12, 5, 19, 8]", listOf("min(scores)", "len(scores)", "max(scores)", "sum(scores)"), 2, "max() возвращает наибольшее значение. Для scores это 19.", "Название нужной функции буквально означает «максимум»."),
        QuizQuestion(6, "Что делает sum([2, 3, 4])?", "", listOf("Возвращает 9", "Возвращает 24", "Возвращает 3", "Возвращает список"), 0, "sum() складывает числа последовательности: 2 + 3 + 4 = 9.", "Сложи все три числа."),
        QuizQuestion(7, "Какой режим open() подходит для чтения текстового файла?", "", listOf("\"w\"", "\"r\"", "\"a\"", "\"x\""), 1, "Режим r открывает файл для чтения. Режим w предназначен для записи и может перезаписать содержимое.", "r — первая буква английского read («читать»)."),
        QuizQuestion(7, "Что вернёт json.loads(text)?", "", listOf("Преобразованный объект Python из JSON-текста", "Всегда файл на диске", "Всегда строку без изменений", "Запущенный модуль"), 0, "json.loads() разбирает JSON-строку и создаёт соответствующий объект Python, например словарь или список.", "loads читает JSON из строки, а не из файлового объекта."),
        QuizQuestion(7, "Для чего обычно используют регулярные выражения?", "", listOf("Для поиска шаблонов в тексте", "Для создания Android-экранов", "Для автоматического исправления любого кода", "Только для математических вычислений"), 0, "Регулярные выражения описывают шаблоны текста, которые можно искать или проверять.", "Например, шаблон может описывать формат номера или последовательность цифр."),
        QuizQuestion(8, "Что создаёт это выражение?", "[n * n for n in range(1, 4)]", listOf("[1, 2, 3]", "[1, 4, 9]", "[0, 1, 4, 9]", "[1, 2, 3, 4]"), 1, "Включение вычисляет квадрат каждого n из range(1, 4), то есть 1, 4 и 9.", "Определи значения n, которые выдаёт range(1, 4), и возведи каждое в квадрат."),
        QuizQuestion(8, "Что делает zip(names, scores)?", "names = [\"Анна\", \"Игорь\"]\nscores = [90, 85]", listOf("Сортирует оба списка", "Объединяет элементы по одинаковым позициям", "Удаляет повторения из списков", "Складывает все числа"), 1, "zip() выдаёт пары элементов с одинаковыми позициями и останавливается по кратчайшей последовательности.", "Подумай, как можно сопоставить имя и балл, не обращаясь к индексам вручную."),
        QuizQuestion(8, "Какой результат у set([2, 2, 1]) по смыслу?", "", listOf("Коллекция с тремя элементами, включая оба значения 2", "Коллекция уникальных значений 1 и 2", "Отсортированный список [1, 2]", "Сумма элементов 5"), 1, "Множество хранит только уникальные элементы, поэтому повторная двойка исчезает.", "Множество подходит для представления уникальных значений."),
        QuizQuestion(9, "Что хранит параметр *args внутри функции?", "def inspect(*args):\n    print(type(args).__name__)", listOf("Словарь именованных аргументов", "Кортеж позиционных аргументов", "Только первый аргумент", "Список локальных переменных"), 1, "*args собирает произвольное количество позиционных аргументов в кортеж.", "Сравни роль одной звёздочки с двумя звёздочками в параметрах функций."),
        QuizQuestion(9, "Как получить значение mode из **kwargs?", "def show(**kwargs):\n    print(kwargs[\"mode\"])", listOf("kwargs.mode", "kwargs[\"mode\"]", "mode(kwargs)", "kwargs(0)"), 1, "**kwargs собирает именованные аргументы в словарь, поэтому значение читают по ключу.", "Вспомни синтаксис доступа к значению по ключу словаря."),
        QuizQuestion(9, "Что особенного в функции, которая использует yield?", "", listOf("Она обязательно возвращает строку", "Она создаёт генератор, выдающий значения по запросу", "Она автоматически запускается в отдельном потоке", "Она может вызываться только один раз в программе"), 1, "yield превращает функцию в генератор, который выдаёт значения по одному при переборе.", "Сравни выдачу одного значения с возвратом готового результата через return."),
        QuizQuestion(10, "Что получает класс Dog от Animal при наследовании?", "class Dog(Animal):\n    pass", listOf("Только имя модуля Animal", "Доступные унаследованные методы и поведение Animal", "Все экземпляры Animal как один объект", "Автоматически созданную базу данных"), 1, "Dog наследует методы и атрибуты класса Animal, доступные через механизм наследования.", "Наследование позволяет специализировать существующий класс, а не копировать его код."),
        QuizQuestion(10, "Для чего используют @property?", "", listOf("Чтобы запускать метод только при импорте модуля", "Чтобы обращаться к методу как к атрибуту и управлять доступом к значению", "Чтобы автоматически превратить любой атрибут в список", "Чтобы создать подкласс"), 1, "@property позволяет использовать метод через синтаксис атрибута, а setter может проверять присваиваемое значение.", "Вспомни, как можно скрыть проверку данных за привычным доступом через точку."),
        QuizQuestion(10, "Что обычно генерирует @dataclass?", "", listOf("Только сетевое соединение", "Методы вроде __init__ и строкового представления", "SQL-таблицу для каждого поля", "Цикл для обхода объекта"), 1, "dataclass автоматически создаёт типичные методы хранения данных, включая инициализацию и представление.", "Подумай, какие повторяющиеся методы приходится писать в простом классе данных."),
        QuizQuestion(11, "Какое исключение возникает при int(\"abc\")?", "", listOf("KeyError", "ValueError", "IndexError", "ZeroDivisionError"), 1, "Строка abc не представляет целое число, поэтому int() вызывает ValueError.", "Тип аргумента — строка, но её содержимое нельзя преобразовать в целое число."),
        QuizQuestion(11, "Когда выполняется блок finally?", "", listOf("Только когда try завершился без ошибок", "Только если сработал except", "При завершении конструкции try в обычном ходе выполнения, независимо от ошибки", "Только при возврате None"), 2, "finally предназначен для завершающих действий и выполняется как при успешном выполнении, так и при исключении.", "Отличай finally от else, который запускается только при отсутствии исключения."),
        QuizQuestion(11, "Что произойдёт, если условие assert ложно?", "", listOf("Python молча продолжит выполнение", "Возникнет AssertionError", "Автоматически запустится except ValueError", "Значение станет True"), 1, "Ложное условие assert вызывает AssertionError и прерывает обычный поток выполнения, если исключение не обработано.", "assert проверяет предположение, которое должно быть истинным в этой точке программы."),
        QuizQuestion(12, "Какой тип обычно возвращает json.loads() для JSON-объекта?", "import json\nvalue = json.loads('{\"name\": \"Оля\"}')", listOf("Словарь Python", "Открытый файл", "Модуль json", "Всегда строку"), 0, "JSON-объект преобразуется в словарь Python; ключ name доступен по индексу словаря.", "JSON-объект заключён в фигурные скобки и содержит пары ключ-значение."),
        QuizQuestion(12, "Что возвращает Path(\"report.csv\").suffix?", "from pathlib import Path", listOf("\"report.csv\"", "\"report\"", "\".csv\"", "None во всех случаях"), 2, "suffix возвращает расширение пути, включая точку: .csv.", "Сравни свойства name, stem и suffix у pathlib.Path."),
        QuizQuestion(12, "Что найдёт re.findall(r\"\\d+\", \"id42x7\")?", "import re", listOf("[\"id\", \"x\"]", "[\"4\", \"2\", \"7\"]", "[\"42\", \"7\"]", "[42, 7]"), 2, "\\d+ находит группы из одной или более цифр подряд, поэтому совпадения — строки \"42\" и \"7\".", "Знак + объединяет соседние цифры в одну группу совпадения."),
        QuizQuestion(13, "Что делает SQL-запрос SELECT name FROM users?", "", listOf("Удаляет таблицу users", "Выбирает столбец name из строк таблицы users", "Создаёт новый столбец name", "Добавляет новую строку"), 1, "SELECT читает данные; FROM указывает таблицу, а name — столбец результата.", "Различай команды чтения данных и команды изменения таблицы."),
        QuizQuestion(13, "Почему Decimal(\"0.1\") + Decimal(\"0.2\") полезен для денег?", "", listOf("Он всегда округляет до целого", "Он представляет эти десятичные значения точно", "Он автоматически загружает валютный курс", "Он работает только с целыми числами"), 1, "Decimal со строковыми значениями сохраняет точное десятичное значение 0.1 и 0.2, их сумма равна 0.3.", "Проблема обычного float связана с двоичным представлением многих десятичных дробей."),
        QuizQuestion(13, "Как получить хост из результата urlparse(url)?", "from urllib.parse import urlparse\nurl = urlparse(\"https://example.com/path\")", listOf("url.path", "url.hostname", "url.scheme", "url.fragment"), 1, "hostname содержит имя хоста example.com; path содержит /path, а scheme — https.", "Сопоставь компоненты адреса с хостом, путём и схемой."),
        QuizQuestion(14, "Какое условие необходимо для обычного бинарного поиска?", "", listOf("Список должен быть отсортирован", "В списке должны быть только строки", "В списке не должно быть чётных чисел", "Список обязан содержать ровно 10 элементов"), 0, "Бинарный поиск отбрасывает половину диапазона по результату сравнения, поэтому порядок элементов должен быть известен.", "Алгоритм решает, в какой половине может находиться цель, сравнивая её со средним элементом."),
        QuizQuestion(14, "Какова временная сложность линейного поиска в худшем случае?", "", listOf("O(1)", "O(log n)", "O(n)", "O(n²)"), 2, "В худшем случае линейный поиск проверяет до n элементов, поэтому время растёт пропорционально n.", "Посчитай, сколько элементов придётся проверить, если цель находится последней или отсутствует."),
        QuizQuestion(14, "Зачем нужна базовая ветка в рекурсивной функции?", "", listOf("Чтобы каждый вызов создавал новый класс", "Чтобы остановить дальнейшие рекурсивные вызовы", "Чтобы сортировать аргументы", "Чтобы автоматически кэшировать результат"), 1, "Базовая ветка возвращает результат без нового рекурсивного вызова и завершает цепочку.", "Без условия остановки рекурсия может продолжаться до RecursionError."),
        QuizQuestion(15, "Что гарантируют аннотации типов Python сами по себе?", "", listOf("Автоматически проверяют все типы при каждом вызове", "Документируют ожидаемые типы, но не включают обязательную runtime-проверку", "Преобразуют строку в число автоматически", "Запрещают вызывать функцию с любыми аргументами"), 1, "Аннотации типов полезны редакторам и анализаторам, но интерпретатор обычно не проверяет их автоматически.", "Отделяй подсказки типов от явной проверки данных в коде."),
        QuizQuestion(15, "Что делает декоратор функции в распространённом сценарии?", "", listOf("Удаляет исходную функцию из памяти", "Принимает функцию и возвращает функцию с дополнительным поведением", "Всегда превращает функцию в асинхронную", "Автоматически создаёт класс"), 1, "Декоратор оборачивает функцию или заменяет её другим вызываемым объектом, добавляя поведение.", "Представь обёртку, которая выполняет действия до и после исходного вызова."),
        QuizQuestion(15, "Зачем использовать with с контекстным менеджером?", "", listOf("Чтобы гарантировать выполнение логики выхода из блока и очистки ресурса", "Чтобы автоматически повторить блок бесконечно", "Чтобы отключить обработку исключений", "Чтобы превратить любой объект в словарь"), 0, "Контекстный менеджер выполняет логику выхода при завершении блока, что помогает освобождать ресурсы и выполнять очистку.", "Подумай о файлах и соединениях, которые нужно корректно закрывать даже при ошибке.")
    )

    private lateinit var root: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var count: TextView
    private lateinit var prompt: TextView
    private lateinit var code: TextView
    private lateinit var optionsBox: LinearLayout
    private lateinit var feedback: TextView
    private lateinit var hintButton: Button
    private lateinit var nextButton: Button
    private var questionIndex = 0
    private var answered = false
    private var score = 0
    private var correctCount = 0
    private var hintShown = false
    private val answeredIds = mutableSetOf<Int>()
    private val earnedIds = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val prefs = getSharedPreferences("python_quest", MODE_PRIVATE)
        score = prefs.getInt("quiz_best_score", 0)
        answeredIds.addAll(prefs.getStringSet("quiz_answered_ids", emptySet())?.mapNotNull { it.toIntOrNull() } ?: emptyList())
        earnedIds.addAll(prefs.getStringSet("quiz_xp_awarded_ids", emptySet())?.mapNotNull { it.toIntOrNull() } ?: emptyList())
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(28))
        }
        scroll.addView(root)
        setContentView(scroll)
        addText(root, "PYTHON QUEST  ·  ПРАКТИКА", 12f, green, true)
        addText(root, "Контроль знаний", 27f, white, true)
        addText(root, "Вопросы по всем 8 разделам. Сначала выбери ответ самостоятельно — затем увидишь объяснение.", 14f, muted, false)
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = questions.size
            progressTintList = android.content.res.ColorStateList.valueOf(green)
            progressBackgroundTintList = android.content.res.ColorStateList.valueOf(panelAlt)
        }
        root.addView(progress, params(6, 4))
        count = addText(root, "", 12f, muted, true)
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(panel, 18)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        root.addView(card, params(8, 8))
        prompt = addText(card, "", 20f, white, true)
        code = addText(card, "", 15f, green, false).apply {
            typeface = Typeface.MONOSPACE
            background = rounded(Color.rgb(7, 12, 8), 10)
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        optionsBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card.addView(optionsBox, params(8, 8))
        feedback = addText(card, "", 14f, muted, false)
        hintButton = makeButton("Подсказка", panelAlt) { showHint() }
        root.addView(hintButton, params(4, 5))
        nextButton = makeButton("Следующий вопрос", green) { nextQuestion() }
        root.addView(nextButton, params(4, 5))
        val back = makeButton("Вернуться к урокам", panelAlt) { finish() }
        root.addView(back, params(4, 0))
        showQuestion()
    }

    private fun showQuestion() {
        if (questionIndex >= questions.size) {
            showSummary()
            return
        }
        val q = questions[questionIndex]
        answered = false
        hintShown = false
        count.text = "Вопрос ${questionIndex + 1} из ${questions.size}  ·  ${sectionNames[q.section]}"
        progress.progress = questionIndex
        prompt.text = q.prompt
        code.text = q.code
        code.visibility = if (q.code.isBlank()) android.view.View.GONE else android.view.View.VISIBLE
        feedback.text = ""
        hintButton.isEnabled = true
        hintButton.alpha = 1f
        nextButton.text = if (questionIndex == questions.lastIndex) "Показать результат" else "Следующий вопрос"
        nextButton.setOnClickListener { nextQuestion() }
        nextButton.isEnabled = false
        optionsBox.removeAllViews()
        q.options.forEachIndexed { index, option ->
            val button = makeButton(option, panelAlt) { chooseAnswer(index) }
            button.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            optionsBox.addView(button, params(3, 4))
        }
    }

    private fun chooseAnswer(index: Int) {
        if (answered || questionIndex !in questions.indices) return
        answered = true
        val q = questions[questionIndex]
        val correct = index == q.answer
        answeredIds.add(questionIndex)
        var firstReward = false
        if (correct) {
            correctCount++
            firstReward = earnedIds.add(questionIndex)
            if (firstReward) {
                val prefs = getSharedPreferences("python_quest", MODE_PRIVATE)
                val newXp = prefs.getInt("xp", 0) + 10
                score = maxOf(score, correctCount)
                prefs.edit()
                    .putInt("xp", newXp)
                    .putStringSet("quiz_xp_awarded_ids", earnedIds.map { it.toString() }.toSet())
                    .putStringSet("quiz_answered_ids", answeredIds.map { it.toString() }.toSet())
                    .putInt("quiz_best_score", maxOf(score, correctCount))
                    .apply()
            }
            feedback.setTextColor(green)
            feedback.text = "Верно! ${if (firstReward) "+10 XP начислено." else "XP за этот вопрос повторно не начисляется."}\n\n${q.explanation}"
        } else {
            feedback.setTextColor(white)
            feedback.text = "Пока неверно. Попробуй ещё раз в следующем прохождении.\n\nОбъяснение: ${q.explanation}"
        }
        score = maxOf(score, correctCount)
        getSharedPreferences("python_quest", MODE_PRIVATE).edit()
            .putStringSet("quiz_answered_ids", answeredIds.map { it.toString() }.toSet())
            .putInt("quiz_best_score", score)
            .apply()
        for (optionIndex in 0 until optionsBox.childCount) {
            val view = optionsBox.getChildAt(optionIndex)
            if (view is Button) {
                view.isEnabled = false
                if (optionIndex == q.answer) view.background = rounded(Color.rgb(35, 83, 48), 12)
                else if (optionIndex == index) view.background = rounded(Color.rgb(89, 43, 43), 12)
            }
        }
        hintButton.isEnabled = false
        nextButton.isEnabled = true
    }

    private fun showHint() {
        if (answered || hintShown) return
        hintShown = true
        feedback.setTextColor(muted)
        feedback.text = "Подсказка: ${questions[questionIndex].hint}"
        hintButton.isEnabled = false
    }

    private fun nextQuestion() {
        if (!answered) return
        questionIndex++
        if (questionIndex < questions.size) showQuestion() else showSummary()
    }

    private fun showSummary() {
        progress.progress = questions.size
        count.text = "Проверка завершена"
        prompt.text = "Результат"
        code.visibility = android.view.View.GONE
        optionsBox.removeAllViews()
        feedback.setTextColor(white)
        feedback.text = "Правильных ответов за эту попытку: $correctCount из ${questions.size}.\n\nНаграда: 10 XP за каждый вопрос, впервые решённый правильно. Повторные ответы не начисляют XP повторно.\n\nМожно пройти проверку ещё раз, чтобы закрепить материал."
        hintButton.visibility = android.view.View.GONE
        nextButton.text = "Пройти ещё раз"
        nextButton.isEnabled = true
        nextButton.setOnClickListener {
            questionIndex = 0
            correctCount = 0
            hintButton.visibility = android.view.View.VISIBLE
            showQuestion()
        }
    }

    private fun makeButton(label: String, color: Int, action: () -> Unit) = Button(this).apply {
        text = label
        textSize = 14f
        isAllCaps = false
        setTextColor(if (color == green) Color.rgb(8, 33, 15) else white)
        background = rounded(color, 12)
        setPadding(dp(12), dp(9), dp(12), dp(9))
        setOnClickListener { action() }
    }

    private fun addText(parent: LinearLayout, value: String, size: Float, color: Int, bold: Boolean): TextView {
        val view = TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            setLineSpacing(dp(2).toFloat(), 1f)
        }
        parent.addView(view, params(4, 7))
        return view
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
