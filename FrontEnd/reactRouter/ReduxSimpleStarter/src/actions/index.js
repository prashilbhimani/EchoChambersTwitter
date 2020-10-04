import axios from 'axios';
export const FETCH_POSTS = 'fetch_posts';
export const CREATE_POSTS = 'create_posts';

const ROOT_URL = 'http://reduxblog.herokuapp.com/api';
const API_KEY = '?key=paperbash1432;'


export function fetchPosts() {
  const request = axios.get(`${ROOT_URL}/posts${API_KEY}`);
  // console.log(`req: ${request}`)
  return {
    type: FETCH_POSTS,
    payload: request
  };
}

export function createPost(values) {
  const request = axios.post(`${ROOT_URL}/posts${API_KEY}`, values);

  return {
    type: CREATE_POST,
    payload: request
  }
}


