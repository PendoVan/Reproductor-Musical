import os
from dotenv import load_dotenv

# Carga variables de entorno desde un archivo .env (si existe)
load_dotenv()

# Directorio donde se guardarán los MP3
BASE_DIR = os.path.dirname(os.path.dirname(__file__))
DOWNLOAD_DIR = os.getenv("DOWNLOAD_DIR") or os.path.join(BASE_DIR, "downloads")

# Calidad del audio (en kbps) usada por FFmpeg a través de yt-dlp
QUALITY = os.getenv("YT_QUALITY", "192")
