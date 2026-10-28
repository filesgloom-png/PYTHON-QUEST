"""Regression tests for the Python code runner."""
import unittest

from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "python"))
import runner  # noqa: E402


class RunCodeTests(unittest.TestCase):
    def test_captures_standard_output(self):
        result = runner.run_code('print("hello")')
        self.assertTrue(result["ok"])
        self.assertEqual(result["output"], "hello\n")

    def test_reports_empty_output(self):
        result = runner.run_code("value = 42")
        self.assertTrue(result["ok"])
        self.assertEqual(result["output"], "Код выполнен. Вывод пустой.")

    def test_reports_syntax_errors(self):
        result = runner.run_code("if True print('broken')")
        self.assertFalse(result["ok"])
        self.assertIn("SyntaxError", result["output"])

    def test_reports_runtime_errors(self):
        result = runner.run_code("print(1 / 0)")
        self.assertFalse(result["ok"])
        self.assertIn("ZeroDivisionError", result["output"])

    def test_caps_large_output(self):
        result = runner.run_code('print("x" * 60000)')
        self.assertTrue(result["ok"])
        self.assertIn("вывод обрезан", result["output"])
        self.assertLess(len(result["output"]), 51000)

    def test_stops_infinite_loops(self):
        result = runner.run_code("while True:\n    pass", timeout_seconds=0.15)
        self.assertFalse(result["ok"])
        self.assertIn("TimeoutError", result["output"])

    def test_restores_trace_after_timeout(self):
        previous_trace = sys.gettrace()
        runner.run_code("while True:\n    pass", timeout_seconds=0.1)
        self.assertIs(sys.gettrace(), previous_trace)
        result = runner.run_code("print('still works')")
        self.assertTrue(result["ok"])
        self.assertEqual(result["output"], "still works\n")


if __name__ == "__main__":
    unittest.main()
