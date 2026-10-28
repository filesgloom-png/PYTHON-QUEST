"""Small runner used by the PYTHON QUEST Android learning app."""
import contextlib
import io
import sys
import time
import traceback


class LimitedStringIO(io.StringIO):
    """Capture output without allowing a snippet to allocate unbounded text."""

    def __init__(self, limit=50_000):
        super().__init__()
        self.limit = limit
        self.written = 0
        self.truncated = False

    def write(self, value):
        remaining = self.limit - self.written
        if remaining > 0:
            chunk = value[:remaining]
            super().write(chunk)
            self.written += len(chunk)
        if len(value) > remaining and not self.truncated:
            super().write("\\n...[вывод обрезан: превышен лимит 50000 символов]...")
            self.truncated = True
        return len(value)


def run_code(source, timeout_seconds=3.0):
    """Execute a snippet with captured, size-limited output and a CPU time limit."""
    output = LimitedStringIO()
    namespace = {"__name__": "__main__"}
    deadline = time.monotonic() + max(0.05, float(timeout_seconds))
    previous_trace = sys.gettrace()

    def enforce_deadline(frame, event, arg):
        if time.monotonic() > deadline:
            raise TimeoutError(
                "Время выполнения истекло. Проверь цикл или упрости вычисления."
            )
        return enforce_deadline

    try:
        sys.settrace(enforce_deadline)
        with contextlib.redirect_stdout(output), contextlib.redirect_stderr(output):
            exec(compile(source, "<python-quest>", "exec"), namespace, namespace)
        result = output.getvalue()
        return {"ok": True, "output": result if result else "Код выполнен. Вывод пустой."}
    except BaseException:
        result = output.getvalue() + traceback.format_exc()
        return {"ok": False, "output": result}
    finally:
        sys.settrace(previous_trace)
