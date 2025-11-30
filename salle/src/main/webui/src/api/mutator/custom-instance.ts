import Axios, {AxiosRequestConfig} from 'axios';

export const customInstance = async <T>(
    config: AxiosRequestConfig,
): Promise<T> => {
    const instance = Axios.create({
        baseURL: '',
        withCredentials: true,
        paramsSerializer: {
            indexes: null
        }
    });

    instance.interceptors.request.use((req) => {
        req.headers.set('HX-Request', 'true');
        return req;
    });

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response?.status === 499) {
                window.location.href = "/index.html";
            } else {
                return Promise.reject(error);
            }
        }
    );

    const response = await instance.request<T>(config);
    return response.data;
};
