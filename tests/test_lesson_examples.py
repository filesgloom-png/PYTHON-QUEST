"""Execute every lesson's starter example and verify its declared output."""
import json
import re
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MAIN_ACTIVITY = ROOT / "app" / "src" / "main" / "java" / "com" / "filesgloom" / "pythonquest" / "MainActivity.kt"
sys.path.insert(0, str(ROOT / "app" / "src" / "main" / "python"))
import runner  # noqa: E402


def _matching_call_end(source, open_paren):
    depth = 0
    in_string = False
    escaped = False
    for index in range(open_paren, len(source)):
        char = source[index]
        if in_string:
            if escaped:
                escaped = False
            elif char == "\\":
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == "(":
            depth += 1
        elif char == ")":
            depth -= 1
            if depth == 0:
                return index
    raise AssertionError("Unclosed Lesson(...) constructor")


def _split_arguments(source):
    args = []
    start = 0
    parens = braces = brackets = 0
    in_string = escaped = False
    for index, char in enumerate(source):
        if in_string:
            if escaped:
                escaped = False
            elif char == "\\":
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == "(":
            parens += 1
        elif char == ")":
            parens -= 1
        elif char == "{":
            braces += 1
        elif char == "}":
            braces -= 1
        elif char == "[":
            brackets += 1
        elif char == "]":
            brackets -= 1
        elif char == "," and parens == braces == brackets == 0:
            args.append(source[start:index].strip())
            start = index + 1
    args.append(source[start:].strip())
    return args


def _decode_kotlin_string(token):
    token = token.strip()
    if len(token) < 2 or token[0] != '"' or token[-1] != '"':
        raise AssertionError(f"Expected a Kotlin string literal, got: {token[:80]!r}")
    # Kotlin uses \$ to escape template interpolation; the resulting string has $.
    return json.loads(token.replace(r"\$", "$"))


def _extract_lessons(source):
    list_start = source.index("private val lessons = listOf(")
    cursor = list_start
    lessons = []
    while True:
        call_start = source.find("Lesson(", cursor)
        if call_start < 0:
            break
        open_paren = call_start + len("Lesson")
        close_paren = _matching_call_end(source, open_paren)
        call_body = source[open_paren + 1:close_paren]
        args = _split_arguments(call_body)
        if len(args) < 7:
            raise AssertionError(f"Lesson constructor has only {len(args)} arguments")
        title = _decode_kotlin_string(args[0])
        example = _decode_kotlin_string(args[3])
        starter = _decode_kotlin_string(args[4])
        expected_match = re.search(
            r'it\.trim\(\)\s*==\s*("(?:\\.|[^"\\])*")',
            args[6],
        )
        if not expected_match:
            raise AssertionError(
                f"Lesson {title!r} does not use a supported exact-output predicate: "
                f"{args[6][:120]!r}"
            )
        expected = _decode_kotlin_string(expected_match.group(1))
        lessons.append((title, example, starter, expected))
        cursor = close_paren + 1
        while cursor < len(source) and source[cursor].isspace():
            cursor += 1
        if cursor < len(source) and source[cursor] == ",":
            cursor += 1
            continue
        if cursor < len(source) and source[cursor] == ")":
            break
        raise AssertionError("Unexpected text after Lesson(...) constructor")
    return lessons


class LessonExampleTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.lessons = _extract_lessons(MAIN_ACTIVITY.read_text(encoding="utf-8"))

    def test_course_has_110_lessons(self):
        self.assertEqual(len(self.lessons), 110)

    def test_every_starter_matches_its_example(self):
        for index, (title, example, starter, _expected) in enumerate(self.lessons, 1):
            with self.subTest(lesson=index, title=title):
                self.assertEqual(starter, example)

    def test_every_lesson_example_runs_and_matches_expected_output(self):
        failures = []
        for index, (title, example, _starter, expected) in enumerate(self.lessons, 1):
            result = runner.run_code(example)
            if not result["ok"]:
                failures.append(
                    f"Lesson {index} ({title}): runner reported an error:\n{result['output']}"
                )
            elif result["output"].strip() != expected:
                failures.append(
                    f"Lesson {index} ({title}): expected {expected!r}, "
                    f"got {result['output'].strip()!r}"
                )
        self.assertFalse(failures, "\n\n".join(failures))


if __name__ == "__main__":
    unittest.main()
