# in each subfolder, there is a file at {subfolder}\src\main\java\fr\black_eyes\lootchest\falleffect\{uknown name}.java
# the {uknown name}.java must be renamed like this: Fall{subfolder}.java
# of course, unknown name is not the real java file name, it's just a placeholder, don't use it in the code
# inside the java file, {uknown name} must be replaced by Fall{subfolder}

import os
import re

def rename_java_file(path):
    for root, dirs, files in os.walk(path):
        root_folder_name = os.path.basename(root)
        for file in files:
            if file.endswith('.java'):
                relative_path = os.path.relpath(root, path)
                subfolder_name = relative_path.split(os.sep)[0] if os.sep in relative_path else root_folder_name
                print(subfolder_name)
                with open(os.path.join(root, file), 'r') as f:
                    content = f.read()
                with open(os.path.join(root, file), 'w') as f:
                    f.write(re.sub("v1_16_R3", subfolder_name, content))

# execute function in current dir
rename_java_file(os.path.dirname(os.path.realpath(__file__)))