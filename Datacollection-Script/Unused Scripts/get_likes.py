import urllib.request as urllib3
import re


def get_user_ids_of_post_likes(post_id):
    json_data = urllib3.urlopen('https://twitter.com/i/activity/favorited_popup?id=' + str(post_id)).read().decode('utf-8')
    print(json_data)
    found_ids = re.findall(r'data-user-id=\\"+\d+', json_data)
    unique_ids = list(set([re.findall(r'\d+', match)[0] for match in found_ids]))
    return unique_ids


# Example:
# https://twitter.com/golan/status/1050170659056189441

unique_ids = get_user_ids_of_post_likes(1050170659056189441)
print(unique_ids)
print(len(unique_ids))

# ['13520332', '416273351', '284966399']
#
# 13520332 +> @TopLeftBrick
# 416273351 => @Berenger_r
# 284966399 => @FFrink