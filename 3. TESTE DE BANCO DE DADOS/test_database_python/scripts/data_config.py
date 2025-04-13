import os
import json
from dotenv import load_dotenv


class DataConfig:
    def __init__(self):
        load_dotenv()

        base_dir = os.path.dirname(__file__)
        config_dir = os.path.join(base_dir, "..", "config")

        self.config_path = os.path.join(config_dir, "config.json")
        self.import_csv_data = os.path.join(config_dir, "import_csv_data.json")

        with open(self.config_path, "r", encoding="utf-8") as f:
            self.general_config = json.load(f)

        with open(self.import_csv_data, "r", encoding="utf-8") as f:
            self.script_config = json.load(f)

    def get_sources(self):
        return self.general_config

    def get_paths(self):
        return self.script_config.get("paths", {})

    def get_mysql_config(self):
        return {
            "host": os.getenv("MYSQL_HOST"),
            "port": int(os.getenv("MYSQL_PORT")),
            "user": os.getenv("MYSQL_USER"),
            "password": os.getenv("MYSQL_PASSWORD"),
            "database": os.getenv("MYSQL_DATABASE"),
            "allow_local_infile": True,
        }
