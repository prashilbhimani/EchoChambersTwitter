import { FETCH_POSTS} from "../actions";
import _ from 'lodash';

export default function (state = {}, action) {
  switch (action.type) {
    case FETCH_POSTS:
      // console.log(action.payload.data); // [post1, post2 etc.]
      /*
          We need to convert [post1, post 2 etc]
          to
          {
            id1: post1
            id2: post 2
            .
            .
            .
          } -- do this from  _ library


          _.mapKeys(posts, 'id');
                     obj    what we wanna make a key
       */
      return _.mapKeys(action.payload.data, 'id');
    default:
      return state;
  }

}