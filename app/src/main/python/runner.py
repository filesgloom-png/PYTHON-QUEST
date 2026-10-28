"""Small runner used by the PYTHON QUEST Android learning app."""
import contextlib
import io
import traceback


def run_code(source):
    output = io.StringIO()
    namespace = {"__name__": "__main__"}
    try:
        with contextlib.redirect_stdout(output), contextlib.redirect_stderr(output):
            exec(compile(source, "<python-quest>", "exec"), namespace, namespace)
        result = output.getvalue()
        return {"ok": True, "output": result if result else "Код выполнен. Вывод пустой."}
    except BaseException:
        result = output.getvalue() + traceback.format_exc()
        return {"ok": False, "output": result}
