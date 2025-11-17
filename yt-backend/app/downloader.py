import os
import subprocess
from typing import Optional

import yt_dlp

from app.config import DOWNLOAD_DIR, QUALITY
from app.utils import crear_directorio_si_no_existe


def buscar_video(cancion: str) -> Optional[str]:
    """Devuelve la URL del primer resultado en YouTube para el término dado."""
    resultado = subprocess.run(
        ["yt-dlp", f"ytsearch1:{cancion}", "--get-id"],
        capture_output=True,
        text=True,
    )
    video_id = resultado.stdout.strip()
    if video_id:
        return f"https://www.youtube.com/watch?v={video_id}"
    return None


def descargar_mp3(url: str) -> str:
    """Descarga el audio de un video de YouTube como MP3 y devuelve el nombre del archivo."""
    crear_directorio_si_no_existe(DOWNLOAD_DIR)

    opciones = {
        "format": "bestaudio/best",
        "outtmpl": os.path.join(DOWNLOAD_DIR, "%(title)s.%(ext)s"),
        "postprocessors": [
            {
                "key": "FFmpegExtractAudio",
                "preferredcodec": "mp3",
                "preferredquality": QUALITY,
            }
        ],
        "quiet": True,
        "noplaylist": True,
    }

    with yt_dlp.YoutubeDL(opciones) as ydl:
        info = ydl.extract_info(url, download=True)
        filename = ydl.prepare_filename(info)

    base_name = os.path.splitext(os.path.basename(filename))[0]
    mp3_name = f"{base_name}.mp3"
    return mp3_name
