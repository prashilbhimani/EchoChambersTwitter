import os
import yaml


class Settings():
    def __new__(self, type=None):
        SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
        CONFIG_FILE = os.path.join(SCRIPT_DIR, 'settings.yaml')
        with open(CONFIG_FILE, 'rb') as f:
            self.all_file = yaml.safe_load(f)
        if type:
            self.settings = self.all_file[type]
        else:
            self.settings = self.all_file
        return self.settings


